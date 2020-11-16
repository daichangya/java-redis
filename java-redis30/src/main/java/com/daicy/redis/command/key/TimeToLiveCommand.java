/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.Request;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.IntegerReply;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import com.daicy.redis.protocal.Reply;

import java.time.Instant;

import static com.daicy.redis.storage.DictKey.safeKey;

public abstract class TimeToLiveCommand implements DBCommand {

    @Override
    public Reply execute(RedisDb db, Request request) {
        DictKey dictKey = safeKey(request.getParamStr(0));
        DictValue value = db.getExpires().get(dictKey);
        if (value != null) {
            return hasExpiredAt(db, value, dictKey);
        } else {
            return notExists();
        }
    }

    protected abstract int timeToLive(DictValue value, Instant now);

    private Reply hasExpiredAt(RedisDb db, DictValue value, DictKey dictKey) {
        Instant now = Instant.now();
        if (!value.isExpired(now)) {
            return new IntegerReply(timeToLive(value, now));
        } else {
            db.getDict().remove(dictKey);
            db.getExpires().remove(dictKey);
            return notExists();
        }
    }

    private Reply notExists() {
        return new IntegerReply(-2);
    }
}
