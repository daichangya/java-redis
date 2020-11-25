package com.daicy.redis.client;

import com.daicy.redis.protocal.MultiBulkRedisMessage;
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
    public void start() throws Exception {
        RedisClient redisClient = new RedisClient();
        String[] commands = "keys *".split("\\s+");
        com.daicy.redis.protocal.RedisMessage redisMessage =
                new MultiBulkRedisMessage(asList(commands).stream().map(com.daicy.redis.protocal.RedisMessage::string).collect(toList()));
        ClientPromise promise = redisClient.send(redisMessage,-1);
        System.out.println(promise.get());
    }
}