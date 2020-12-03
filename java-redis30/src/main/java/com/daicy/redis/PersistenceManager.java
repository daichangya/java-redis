/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis;

import com.daicy.redis.command.RedisCommand;
import com.daicy.redis.persistence.RDBInputStream;
import com.daicy.redis.persistence.RDBOutputStream;
import com.daicy.redis.protocal.*;
import com.daicy.redis.protocal.io.RedisSourceInputStream;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.remoting.transport.netty4.ClientSession;
import com.google.common.collect.Lists;
import io.netty.channel.DefaultFileRegion;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.daicy.redis.RedisConstants.*;
import static java.util.Objects.requireNonNull;

public class PersistenceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceManager.class);

    private static final int MAX_FRAME_SIZE = 1024 * 1024 * 100;

    private static final int RDB_VERSION = 6;

    private OutputStream output;
    private final DefaultRedisServerContext redisServerContext;
    private final String dumpFile;
    private final String redoFile;
    private File tempAofFile;
    private final int syncPeriod;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public PersistenceManager(DefaultRedisServerContext redisServerContext, DBConfig config) {
        this.redisServerContext = requireNonNull(redisServerContext);
        this.dumpFile = config.getRdbFile();
        this.redoFile = config.getAofFile();
        this.syncPeriod = config.getSyncPeriod();
    }

    public void start() {
        importRDB(new File(dumpFile));
        importRedo();
        createRedo();
        executor.scheduleWithFixedDelay(this::run, syncPeriod, syncPeriod, TimeUnit.SECONDS);
        LOGGER.info("Persistence manager started");
    }

    public void stop() {
        executor.shutdown();
        closeRedo();
        exportRDB();
        LOGGER.info("Persistence manager stopped");
    }

    void run() {
        exportRDB();
        createRedo();
    }


    public void importRDB(File file) {
        if (file.exists()) {
            try (InputStream fileInputStream = new FileInputStream(file)) {
                RDBInputStream rdb = new RDBInputStream(fileInputStream);
//                try {
//                    byte[] bytes1024 = rdb.read(5);
//                    System.out.println(bytes1024);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                rdb.parse(redisServerContext.getDatabases());
                LOGGER.info("RDB file imported");
            } catch (IOException e) {
                LOGGER.error("error reading RDB", e);
            }
        }
    }

    public void append(Request request) {
        if (output != null) {
            executor.submit(() -> appendRedo(request));
        }
    }

    public void exportRDBBg() {
        executor.submit(() -> exportRDB());
    }


    private void importRedo() {
        File file = new File(redoFile);
        if (file.exists()) {
            try (FileInputStream redo = new FileInputStream(file)) {
                RedisParser parse = new RedisParser(MAX_FRAME_SIZE, new RedisSourceInputStream(redo));

                while (true) {
                    RedisMessage redisMessage = parse.next();
                    if (redisMessage.getType() == RedisMessageType.UNKNOWN) {
                        break;
                    }
                    LOGGER.info("command: {}", redisMessage);

                    processCommand(redisMessage);
                }
            } catch (IOException e) {
                LOGGER.error("error reading AOF file", e);
            }
        }
    }

    public void processCommand(RedisMessage redisMessage) {
        Request request = toRequest((MultiBulkRedisMessage) redisMessage, redisServerContext);
        RedisCommand redisCommand = redisServerContext.getRedisCommand(request.getCommand());
        RedisMessage reply = redisCommand.execute(request);
    }

    public Request toRequest(MultiBulkRedisMessage multiBulkRedisMessage, DefaultRedisServerContext redisServerContext) {
        if (null == multiBulkRedisMessage || CollectionUtils.isEmpty(multiBulkRedisMessage.data())) {
            return null;
        }
        List<String> params = multiBulkRedisMessage.data().stream()
                .map(redisMessage -> ((BulkRedisMessage) redisMessage).data()).collect(Collectors.toList());
        RedisClientSession clientSession = new RedisClientSession("dummy", null);
        return new DefaultRequest(params.get(0), params.subList(1, params.size()), clientSession, redisServerContext);
    }

    private void createRedo() {
        try {
            closeRedo();
            tempAofFile = new File(String.format("temp-%s.aof", Thread.currentThread().getId()));
            tempAofFile.createNewFile();
            output = new FileOutputStream(tempAofFile);
            LOGGER.info("AOF file created");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void closeRedo() {
        IOUtils.closeQuietly(output);
        if (null != tempAofFile) {
            tempAofFile.renameTo(new File(redoFile));
        }
        LOGGER.debug("AOF file closed");
    }

    public void exportRDB() {
        if (redisServerContext.isRdbIng()) {
            return;
        }
        redisServerContext.setRdbIng(true);
        File tempFile;
        FileOutputStream fileOutputStream = null;
        String tempDumpFile = String.format("temp-%s.rdb", Thread.currentThread().getId());
        try {
            tempFile = new File(tempDumpFile);
            tempFile.createNewFile();
            fileOutputStream = new FileOutputStream(tempFile);
            RDBOutputStream rdb = new RDBOutputStream(fileOutputStream);
            rdb.preamble(RDB_VERSION);
            List<RedisDb> databases = redisServerContext.getDatabases();
            List<Map<DictKey, DictValue>> dictList = Lists.newArrayList();
            List<Map<DictKey, DictValue>> expireList = Lists.newArrayList();
            for (int i = 0; i < databases.size(); i++) {
                RedisDb db = databases.get(i);
                if (!db.getDict().isEmpty()) {
                    dictList.add(db.getDict().fork());
                    expireList.add(db.getExpires().fork());
                }
            }
            for (int i = 0; i < dictList.size(); i++) {
                rdb.select(i);
                Map<DictKey, DictValue> dict = dictList.get(i);
                Map<DictKey, DictValue> expires = expireList.get(i);
                rdb.dabatase(dict, expires);
                dict = null;
                expires = null;
            }
            rdb.end();
            tempFile.renameTo(new File(dumpFile));
            redisServerContext.setRdbIng(false);
            LOGGER.info("RDB file exported");
            rdbToSlave();
        } catch (IOException e) {
            LOGGER.error("error writing to RDB file", e);
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

    private void rdbToSlave() {
        RandomAccessFile raf = null;
        try {
            // 1. 通过 RandomAccessFile 打开一个文件.
            raf = new RandomAccessFile(dumpFile, "r");
            long length = raf.length();

            for (ClientSession clientSession : redisServerContext.getSlaves()) {
                RedisClientSession redisClientSession = (RedisClientSession) clientSession;
                if (redisClientSession.getReplstate() == REDIS_REPL_WAIT_BGSAVE_END) {
                    redisClientSession.setReplstate(REDIS_REPL_SEND_BULK);
                    redisClientSession.getChannel().write(String.format("$%s\r\n",length));
                    redisClientSession.getChannel().write(
                            new DefaultFileRegion(raf.getChannel(), 0, length));
                    redisClientSession.getChannel().writeAndFlush("\r\n");
                    redisClientSession.setReplstate(REDIS_REPL_ONLINE);
                }

            }
        } catch (Exception ex) {
            LOGGER.error("rdbtoslave error", ex);
        } finally {
            IOUtils.closeQuietly(raf);
        }
    }


    private void appendRedo(Request request) {
        try {
            int db = request.getClientSession().getDictNum();
            output.write(String.format("*2\r\n$6\r\nselect\r\n$%s\r\n%s\r\n"
                    , String.valueOf(db).length(), db).getBytes());

            byte[] buffer = DefaultRequest.toMultiBulkRedisMessage(request).encode();
            output.write(buffer);
            output.flush();
            LOGGER.debug("new command: " + request.getCommand());
        } catch (IOException e) {
            LOGGER.error("error writing to AOF file", e);
        }
    }

}