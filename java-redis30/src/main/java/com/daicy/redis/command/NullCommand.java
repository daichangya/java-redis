/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;

import com.daicy.redis.Request;
import com.daicy.redis.protocal.ErrorReply;
import com.daicy.redis.protocal.Reply;


class NullCommand implements RedisCommand {
    @Override
    public Reply execute(Request request) {
        return new ErrorReply("ERR unknown command '" + request.getCommand() + "'");
    }
}
