/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.protocal.BulkReply;
import com.daicy.redis.protocal.MultiBulkReply;
import com.daicy.redis.protocal.Reply;
import com.google.common.collect.Lists;

import java.time.Clock;
import java.util.List;

@Command("time")
public class TimeCommand implements RedisCommand {

    private static final int SCALE = 1000;

    @Override
    public Reply execute(Request request) {
        long currentTimeMillis = Clock.systemDefaultZone().millis();
        List<Reply> replyList = Lists.newArrayList();
        replyList.add(new BulkReply(seconds(currentTimeMillis)));
        replyList.add(new BulkReply(microseconds(currentTimeMillis)));
        return new MultiBulkReply(replyList);
    }

    private static String seconds(long currentTimeMillis) {
        return String.valueOf(currentTimeMillis / SCALE);
    }

    // XXX: Java doesn't have microsecond accuracy
    private static String microseconds(long currentTimeMillis) {
        return String.valueOf((currentTimeMillis % SCALE) * SCALE);
    }
}
