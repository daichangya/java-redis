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
import com.daicy.redis.database.Database;
import com.daicy.redis.database.DatabaseKey;
import com.daicy.redis.database.DatabaseValue;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

import static com.daicy.redis.RedisConstants.OK;


@Command("set")
@ParamLength(2)
public class SetCommand implements DBCommand {

    @Override
    public RedisMessage execute(Database db, Request request) {
        return Try.of(() -> onSuccess(db, request)).recover(this::onFailure)
                .get();
    }

    private RedisMessage onSuccess(Database db, Request request) {
        DatabaseKey key = DatabaseKey.safeKey(request.getParamStr(0));
        DatabaseValue value = DatabaseValue.string(request.getParamStr(1));
        int a = 10/0;
        return value.equals(saveValue(db, key, value)) ? OK : FullBulkStringRedisMessage.NULL_INSTANCE;
    }

    private RedisMessage onFailure(Throwable e) {
        return new ErrorRedisMessage("error: " + e.getMessage());
    }

    private DatabaseValue saveValue(Database db, DatabaseKey key, DatabaseValue value) {
        DatabaseValue savedValue = null;
        savedValue = putValue(db, key, value);
        return savedValue;
    }

    private DatabaseValue putValue(Database db, DatabaseKey key, DatabaseValue value) {
        db.put(key, value);
        return value;
    }
}
