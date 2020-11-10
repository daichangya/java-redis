/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import io.netty.handler.codec.redis.FixedRedisMessagePool;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

@Command("ping")
public class PingCommand implements RedisCommand {

    public static final SimpleStringRedisMessage PONG =
            FixedRedisMessagePool.INSTANCE.getSimpleString("PONG");

    @Override
    public RedisMessage execute(Request request) {
        if (request.getLength() > 0) {
            return request.getParam(0);
        } else {
            return PONG;
        }
    }
}
