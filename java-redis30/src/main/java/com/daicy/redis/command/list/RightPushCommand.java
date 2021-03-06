/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.list;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.protocal.RedisMessage;

import java.util.List;

import static com.daicy.redis.storage.DictKey.safeKey;

@Command("rpush")
@ParamLength(2)
@ParamType(DataType.LIST)
public class RightPushCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        List<String> paramsStrList = request.getParamsStrList();
        DictValue result = db.getDict().merge(db,safeKey(paramsStrList.get(0)),
                DictValue.list(paramsStrList.subList(1, paramsStrList.size())),
                (oldValue, newValue) -> {
                    oldValue.getList().addAll(oldValue.getList().size(), newValue.getList());
                    return oldValue;
                });

        return new IntegerRedisMessage(result.getList().size());
    }
}
