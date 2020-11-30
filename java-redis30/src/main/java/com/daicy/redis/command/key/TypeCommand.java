/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;

@ReadOnly
@Command("type")
@ParamLength(1)
public class TypeCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        DictValue value =
                db.lookupKeyOrExpire(DictKey.safeKey(request.getParamStr(0)));
        if (value == null) {
            return new BulkRedisMessage(DataType.NONE.text());
        } else {
            return new BulkRedisMessage(value.getType().text());
        }
    }
}
