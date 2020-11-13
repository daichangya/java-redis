/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.string;


import com.daicy.function.Try;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.Dict;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

import static com.daicy.redis.RedisConstants.OK;


/**
 * @author daichangya
 * SET key value [NX] [XX] [EX <seconds>] [PX <milliseconds>]
 * http://redisdoc.com/string/set.html
 */
@Command("set")
@ParamLength(2)
public class SetCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        return Try.of(() -> onSuccess(db.getDict(), request)).recover(this::onFailure)
                .get();
    }

    private RedisMessage onSuccess(Dict db, Request request) {
        DictKey key = DictKey.safeKey(request.getParamStr(0));
        DictValue value = DictValue.string(request.getParamStr(1));
        return value.equals(saveValue(db, key, value)) ? OK : FullBulkStringRedisMessage.NULL_INSTANCE;
    }

    private RedisMessage onFailure(Throwable e) {
        return new ErrorRedisMessage("error: " + e.getMessage());
    }

    private DictValue saveValue(Dict db, DictKey key, DictValue value) {
        DictValue savedValue = null;
        savedValue = putValue(db, key, value);
        return savedValue;
    }

    private DictValue putValue(Dict db, DictKey key, DictValue value) {
        db.put(key, value);
        return value;
    }
}
