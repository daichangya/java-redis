/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.RedisDb;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

import static com.daicy.redis.RedisConstants.OK;
import static com.daicy.redis.storage.DictKey.safeKey;

@Command("rename")
@ParamLength(2)
public class RenameCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        DictKey from = safeKey(request.getParamStr(0));
        DictKey to = safeKey(request.getParamStr(1));
        if (db.getDict().rename(from, to)) {
            db.getExpires().rename(from, to);
            return OK;
        } else {
            return new SimpleStringRedisMessage("ERR no such key");
        }
    }
}