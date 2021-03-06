/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;

import com.daicy.redis.Request;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;


class NullCommand implements RedisCommand {
    @Override
    public RedisMessage execute(Request request) {
        return new ErrorRedisMessage("ERR unknown command '" + request.getCommand() + "'");
    }
}
