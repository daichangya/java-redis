package com.daicy.redis.client;

import com.daicy.redis.client.utils.ByteBufUtils;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.remoting.transport.netty4.client.ClientPromise;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.client
 * @date:11/23/20
 */
public class RedisClientTest {

    @Test
    public void testKeys() throws Exception {
        System.out.println(String.format("$%s\r\n",88));
        RedisClient redisClient = new RedisClient();
        ClientPromise<RedisMessage> promise = redisClient.sendMessage("set daicy 88");
        System.out.println(new String(promise.get().encode()));

        promise = redisClient.sendMessage("get daicy");
        System.out.println(new String(promise.get().encode()));
        redisClient.shutdown();
    }

    @Test
    public void testPsync() throws Exception {
        System.out.println(String.format("$%s\r\n",88));
        RedisClient redisClient = new RedisClient();
        ClientPromise<RedisMessage> promise = redisClient.sendMessage("PSYNC ? -1");
        System.out.println(new String(promise.get().encode()));

        redisClient.shutdown();
        Thread.sleep(1000000);
    }

    @Test
    public void testSync() throws Exception {
        System.out.println(String.format("$%s\r\n",88));
        RedisClient redisClient = new RedisClient();
        ClientPromise<RedisMessage> promise = redisClient.send("sync",ByteBufUtils.toByteBuf("sync\r\n"),99999);
        System.out.println(new String(promise.get().encode()));
        redisClient.shutdown();
    }

}