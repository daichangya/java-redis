package com.daicy.redis;

import com.daicy.redis.client.utils.RedisMessageUtils;
import com.daicy.remoting.transport.netty4.ClientSession;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/20/20
 */
public class Replication {
    public static void replicationFeedSlaves(Request request) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        Set<ClientSession> slaves = redisServerContext.getSlaves();
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
        Set<ClientSession> slaves = redisServerContext.getSlaves();
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
    }

    public static void replicationSetMaster(String host, String port) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        redisServerContext.setMasterhost(host);
        redisServerContext.setMasterport(port);
        RedisClientSession redisClientSession = redisServerContext.getMaster();
        if (null != redisClientSession) {
            Replication.freeClient(redisClientSession);
        }
        Replication.disconnectSlaves();
    }
}
