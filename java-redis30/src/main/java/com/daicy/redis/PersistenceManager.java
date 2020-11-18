/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis;

import com.daicy.redis.command.RedisCommand;
import com.daicy.redis.persistence.RDBInputStream;
import com.daicy.redis.persistence.RDBOutputStream;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.protocal.RedisMessageType;
import com.daicy.redis.protocal.RedisParser;
import com.daicy.redis.protocal.io.RedisSourceInputStream;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.RedisMessageUtils;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class PersistenceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceManager.class);

    private static final int MAX_FRAME_SIZE = 1024 * 1024 * 100;

    private static final int RDB_VERSION = 6;

    private OutputStream output;
    private final DefaultRedisServerContext redisServerContext;
    private final String dumpFile;
    private final String redoFile;
    private final int syncPeriod;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public PersistenceManager(DefaultRedisServerContext redisServerContext, DBConfig config) {
        this.redisServerContext = requireNonNull(redisServerContext);
        this.dumpFile = config.getRdbFile();
        this.redoFile = config.getAofFile();
        this.syncPeriod = config.getSyncPeriod();
    }

    public void start() {
        importRDB();
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


    private void importRDB() {
        File file = new File(dumpFile);
        if (file.exists()) {
            try (InputStream fileInputStream = new FileInputStream(file)) {
                RDBInputStream rdb = new RDBInputStream(fileInputStream);
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

                    processCommand((MultiBulkRedisMessage) redisMessage);
                }
            } catch (IOException e) {
                LOGGER.error("error reading AOF file", e);
            }
        }
    }

    private void processCommand(RedisMessage redisMessage) {
        Request request = RedisMessageUtils.toRequest((MultiBulkRedisMessage) redisMessage, redisServerContext);
        RedisCommand redisCommand = redisServerContext.getRedisCommand(request.getCommand());
        RedisMessage reply = redisCommand.execute(request);
    }

    private void createRedo() {
        try {
            closeRedo();
            output = new FileOutputStream(redoFile);
            LOGGER.info("AOF file created");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void closeRedo() {
        try {
            if (output != null) {
                output.close();
                output = null;
                LOGGER.debug("AOF file closed");
            }
        } catch (IOException e) {
            LOGGER.error("error closing AOF file", e);
        }
    }

    private void exportRDB() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(dumpFile)) {
            RDBOutputStream rdb = new RDBOutputStream(fileOutputStream);
            rdb.preamble(RDB_VERSION);
            List<RedisDb> databases = redisServerContext.getDatabases();
            for (int i = 0; i < databases.size(); i++) {
                RedisDb db = databases.get(i);
                if (!db.getDict().isEmpty()) {
                    rdb.select(i);
                    rdb.dabatase(db);
                }
            }
            rdb.end();
            LOGGER.info("RDB file exported");
        } catch (IOException e) {
            LOGGER.error("error writing to RDB file", e);
        }
    }


    private void appendRedo(Request request) {
        try {
            int db = request.getClientSession().getDictNum();
            output.write(String.format("*2\r\n$6\r\nselect\r\n$%s\r\n%s\r\n"
                    , String.valueOf(db).length(), db).getBytes());

            byte[] buffer = RedisMessageUtils.toMultiBulkRedisMessage(request).encode();
            output.write(buffer);
            output.flush();
            LOGGER.debug("new command: " + request.getCommand());
        } catch (IOException e) {
            LOGGER.error("error writing to AOF file", e);
        }
    }

}