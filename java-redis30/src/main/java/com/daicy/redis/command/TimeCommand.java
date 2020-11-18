/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.google.common.collect.Lists;

import java.time.Clock;
import java.util.List;

@Command("time")
public class TimeCommand implements RedisCommand {

    private static final int SCALE = 1000;

    @Override
    public RedisMessage execute(Request request) {
        long currentTimeMillis = Clock.systemDefaultZone().millis();
        List<RedisMessage> replyList = Lists.newArrayList();
        replyList.add(new BulkRedisMessage(seconds(currentTimeMillis)));
        replyList.add(new BulkRedisMessage(microseconds(currentTimeMillis)));
        return new MultiBulkRedisMessage(replyList);
    }

    private static String seconds(long currentTimeMillis) {
        return String.valueOf(currentTimeMillis / SCALE);
    }

    // XXX: Java doesn't have microsecond accuracy
    private static String microseconds(long currentTimeMillis) {
        return String.valueOf((currentTimeMillis % SCALE) * SCALE);
    }
}
