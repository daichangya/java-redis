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
import com.daicy.redis.utils.RedisMessageUtils;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

@ReadOnly
@Command("sunion")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetUnionCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        List<String> paramsStrList = request.getParamsStrList();
        Set<String> result = Sets.newHashSet();
        for (int i = 0; i < paramsStrList.size(); i++) {
            Set<String> removeSet = db.getDict().getOrDefault(
                    DictKey.safeKey(paramsStrList.get(0)), DictValue.EMPTY_SET).getSet();
            result.addAll(removeSet);
        }
        return RedisMessageUtils.toRedisMessage(result);
    }
}
