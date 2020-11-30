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
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;

import java.util.Set;

import static com.daicy.redis.protocal.RedisMessageConstants.ONE;
import static com.daicy.redis.protocal.RedisMessageConstants.ZERO;

@ReadOnly
@Command("sismember")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetIsMemberCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        Set<String> stringSet = db.lookupKeyOrDefault(request.getParamStr(0),
                DictValue.EMPTY_SET).getSet();
        if (stringSet.contains(request.getParamStr(1))) {
            return ONE;
        } else {
            return ZERO;
        }
    }
}
