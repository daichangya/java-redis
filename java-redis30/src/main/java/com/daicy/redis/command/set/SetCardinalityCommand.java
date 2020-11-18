/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.set;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.RedisDb;

import java.util.Set;

@ReadOnly
@Command("scard")
@ParamLength(1)
@ParamType(DataType.SET)
public class SetCardinalityCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        Set<String> stringSet = db.getDict().getSet(request.getParamStr(0));
        return new IntegerRedisMessage(stringSet.size());
    }
}
