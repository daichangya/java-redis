package com.daicy.redis.client;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.client
 * @date:11/6/20
 */
public class ClientTest {

    @Test
    public void testLettuce(){
        RedisClient client = RedisClient.create("redis://localhost");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands sync = connection.sync();
//        sync.lpush("daicy","1","2","3");
        List<String> list = sync.lrange("daicy",0,10);
        String result = sync.set("client","lettuce");
        String value = (String) sync.get("client");
//        System.out.println(result);
//        System.out.println(value);
    }


    @Test
    public void testJedis(){
        Jedis jedis = new Jedis("localhost");
        String result = jedis.set("client","jedis");
        String value = (String) jedis.get("client");
//        jedis.lpush();
        System.out.println(result);
        System.out.println(value);
    }
}