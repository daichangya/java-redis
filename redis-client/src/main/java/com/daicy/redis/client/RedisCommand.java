package com.daicy.redis.client;

import com.daicy.redis.protocal.RedisMessage;
import com.daicy.remoting.transport.netty4.client.ClientPromise;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.remoting.transport.netty4.redisclient
 * @date:11/25/20
 */
public class RedisCommand<T> {
    private final T redisMessage;

    private final String commandName;

    private final ClientPromise clientPromise;

    public RedisCommand(String commandName, T redisMessage, ClientPromise clientPromise) {
        this.redisMessage = redisMessage;
        this.commandName = commandName.toLowerCase();
        this.clientPromise = clientPromise;
    }

    public T getRedisMessage() {
        return redisMessage;
    }

    public ClientPromise getClientPromise() {
        return clientPromise;
    }


    public String getCommandName() {
        return commandName;
    }
}
