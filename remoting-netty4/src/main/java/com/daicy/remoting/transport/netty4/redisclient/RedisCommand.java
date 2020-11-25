package com.daicy.remoting.transport.netty4.redisclient;

import com.daicy.remoting.transport.netty4.client.ClientPromise;
import io.netty.handler.codec.redis.RedisMessage;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.remoting.transport.netty4.redisclient
 * @date:11/25/20
 */
public class RedisCommand {
    private RedisMessage redisMessage;

    private ClientPromise clientPromise;

    public RedisCommand(RedisMessage redisMessage, ClientPromise clientPromise) {
        this.redisMessage = redisMessage;
        this.clientPromise = clientPromise;
    }

    public RedisMessage getRedisMessage() {
        return redisMessage;
    }

    public void setRedisMessage(RedisMessage redisMessage) {
        this.redisMessage = redisMessage;
    }

    public ClientPromise getClientPromise() {
        return clientPromise;
    }

    public void setClientPromise(ClientPromise clientPromise) {
        this.clientPromise = clientPromise;
    }
}
