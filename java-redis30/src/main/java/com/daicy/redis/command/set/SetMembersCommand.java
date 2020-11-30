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
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.client.utils.RedisMessageUtils;

import java.util.Set;

@ReadOnly
@Command("smembers")
@ParamLength(1)
@ParamType(DataType.SET)
public class SetMembersCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        Set<String> stringSet = db.lookupKeyOrDefault(request.getParamStr(0),
                DictValue.EMPTY_SET).getSet();
        return RedisMessageUtils.toRedisMessage(stringSet);
    }
}
