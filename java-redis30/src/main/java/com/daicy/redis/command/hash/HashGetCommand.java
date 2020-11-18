/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.hash;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;

import static com.daicy.redis.protocal.RedisMessageConstants.NULL;
import static com.daicy.redis.storage.DictKey.safeKey;

@ReadOnly
@Command("hget")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashGetCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        DictKey dictKey = safeKey(request.getParamStr(0));
        DictValue dictValue = db.getDict().getOrDefault(dictKey, DictValue.EMPTY_HASH);
        String value = dictValue.getHash().get(request.getParamStr(1));
        if (null == value) {
            return NULL;
        } else {
            return new BulkRedisMessage(value);
        }
    }
}
