/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.set;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;

import java.util.List;
import java.util.Set;

import static com.daicy.redis.storage.DictKey.safeKey;

@Command("sadd")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetAddCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        List<String> paramsStrList = request.getParamsStrList();
        DictKey dictKey = safeKey(paramsStrList.get(0));
        DictValue dictValue = DictValue.set(paramsStrList.subList(1, paramsStrList.size()));
        DictValue oldValue = db.getDict().putIfAbsent(dictKey,dictValue);
        if(null == oldValue){
            return new IntegerRedisMessage(dictValue.getSet().size());
        }
        Set<String> oldValueSet = oldValue.getSet();
        int oldSize = oldValueSet.size();
        oldValueSet.addAll(dictValue.getSet());
        return new IntegerRedisMessage(oldValueSet.size() - oldSize);
    }
}
