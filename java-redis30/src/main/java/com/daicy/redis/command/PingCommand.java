/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.protocal.BulkReply;
import com.daicy.redis.protocal.Reply;

import static com.daicy.redis.protocal.ReplyConstants.PONG;


@Command("ping")
public class PingCommand implements RedisCommand {

    @Override
    public Reply execute(Request request) {
        if (request.getLength() > 0) {
            return new BulkReply(request.getParamStr(0));
        } else {
            return PONG;
        }
    }
}
