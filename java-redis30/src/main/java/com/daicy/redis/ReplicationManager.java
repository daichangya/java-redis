package com.daicy.redis;

import com.daicy.redis.client.RedisClient;
import com.daicy.redis.client.utils.ByteBufUtils;
import com.daicy.redis.protocal.BulkByteRedisMessage;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.remoting.transport.netty4.ClientSession;
import com.daicy.remoting.transport.netty4.client.ClientBuilder;
import com.daicy.remoting.transport.netty4.client.ClientPromise;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static com.daicy.redis.RedisConstants.REDIS_REPL_CONNECT;
import static com.daicy.redis.RedisConstants.REDIS_REPL_CONNECTING;
import static com.daicy.redis.RedisConstants.REDIS_REPL_TRANSFER;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/20/20
 */
public class ReplicationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationManager.class);

    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

//    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    static {
//        int syncPeriod = DefaultRedisServerContext.getInstance().getDbConfig().getSyncPeriod();
        scheduledExecutor.scheduleWithFixedDelay(ReplicationManager::run, 1, 1, TimeUnit.SECONDS);
    }

    static void run() {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        if (redisServerContext.getRepl_state() == REDIS_REPL_CONNECT) {
            connectWithMaster(redisServerContext.getMasterhost(), redisServerContext.getMasterport());
        }
        if (redisServerContext.getRepl_state() == REDIS_REPL_CONNECTING) {
            sendSync();
        }
//        Request request = new DefaultRequest("PING", null,
//                null, redisServerContext);
//        ReplicationManager.replicationFeedSlaves(request);
    }


    public static void replicationFeedSlaves(Request request) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        Set<RedisClientSession> slaves = redisServerContext.getSlaves();
        if (CollectionUtils.isEmpty(slaves)) {
            return;
        }
        byte[] selectBytes = new byte[0];
        if (null != request.getClientSession()) {
            int db = request.getClientSession().getDictNum();
            selectBytes = String.format("*2\r\n$6\r\nselect\r\n$%s\r\n%s\r\n"
                    , String.valueOf(db).length(), db).getBytes();
        }
        byte[] buffer = DefaultRequest.toMultiBulkRedisMessage(request).encode();
        for (ClientSession clientSession : slaves) {
            RedisClientSession redisClientSession = (RedisClientSession) clientSession;
            if (selectBytes.length > 0) {
                redisClientSession.getChannel().write(selectBytes);
            }
            redisClientSession.getChannel().writeAndFlush(buffer);
        }
    }

    public static void disconnectSlaves() {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        Set<RedisClientSession> slaves = redisServerContext.getSlaves();
        if (CollectionUtils.isEmpty(slaves)) {
            return;
        }
        for (ClientSession clientSession : slaves) {
            RedisClientSession redisClientSession = (RedisClientSession) clientSession;
            freeClient(redisClientSession);
        }
    }

    public static void freeClient(RedisClientSession redisClientSession) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        redisServerContext.delClient(redisClientSession);
        redisClientSession.getChannel().close();
        pubsubUnsubscribeAllChannels(redisClientSession);
        pubsubUnsubscribeAllPatterns(redisClientSession);
    }

    public static void pubsubUnsubscribeAllChannels(RedisClientSession clientSession) {
        List<String> channels = Lists.newArrayList(clientSession.getPubsubChannels());
        for (String channel : channels) {
            pubsubUnsubscribeChannel(clientSession, channel);
        }
    }

    public static void pubsubUnsubscribeChannel(RedisClientSession clientSession,String channel) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        if (clientSession.getPubsubChannels().remove(channel)) {
            redisServerContext.getPubsubChannels().get(channel).remove(clientSession.getId());
        }
    }

    public static void pubsubUnsubscribeAllPatterns(RedisClientSession clientSession) {
        List<String> channels = Lists.newArrayList(clientSession.getPubsubPatterns());
        for (String channel : channels) {
            patternUnsubscribeChannel(clientSession, channel);
        }
    }

    public static void patternUnsubscribeChannel(RedisClientSession clientSession, String channel) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        if (clientSession.getPubsubPatterns().remove(channel)) {
            redisServerContext.getPubsubPatterns().get(channel).remove(clientSession.getId());
        }
    }


    public static void replicationSetMaster(String host, String port) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        redisServerContext.setMasterhost(host);
        redisServerContext.setMasterport(port);
        RedisClientSession redisClientSession = redisServerContext.getMaster();
        if (null != redisClientSession) {
            ReplicationManager.freeClient(redisClientSession);
        }
        ReplicationManager.disconnectSlaves();
        redisServerContext.setRepl_state(REDIS_REPL_CONNECT);
    }

    public static void connectWithMaster(String host, String port) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        ClientBuilder clientBuilder = ClientBuilder.forHostPort(host, Integer.parseInt(port));
        try {
            SlaveRedisClient slaveRedisClient = new SlaveRedisClient(clientBuilder);
            redisServerContext.setSlaveRedisClient(slaveRedisClient);
            redisServerContext.setMaster(
                    redisServerContext.newSession(slaveRedisClient.getClient().getChannel()));
        } catch (Exception e) {
            LOGGER.error("new SlaveRedisClient error ", e);
        }

        redisServerContext.setRepl_state(REDIS_REPL_CONNECTING);
    }

    static void sendSync() {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        redisServerContext.setRepl_state(REDIS_REPL_TRANSFER);
        ClientPromise<RedisMessage> promise = redisServerContext.getSlaveRedisClient()
                .send("sync", ByteBufUtils.toByteBuf("sync\r\n"),99999);
        try {
            BulkByteRedisMessage response = (BulkByteRedisMessage) promise.get();
            File file = new File("temp-0.rdb");
            FileUtils.writeByteArrayToFile(file,
                    response.data());
            redisServerContext.initFactory();
            redisServerContext.importRDB(file);
        } catch (Exception e) {
            LOGGER.error("sendSync error", e);
        }
    }

    public static void replicationUnsetMaster(){
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        redisServerContext.setMasterhost(null);
        redisServerContext.setMasterport(null);
        redisServerContext.getSlaveRedisClient().shutdown();
        freeClient(redisServerContext.getMaster());
        redisServerContext.setRepl_state(REDIS_REPL_CONNECT);
    }
}
