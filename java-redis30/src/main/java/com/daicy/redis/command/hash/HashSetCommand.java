/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.hash;


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
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import static com.daicy.redis.storage.DictKey.safeKey;
import static com.daicy.redis.storage.DictValue.hash;

@Command("hset")
@ParamLength(3)
@ParamType(DataType.HASH)
public class HashSetCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {

        DictValue dictValue = hash(ImmutableMap.of(request.getParamStr(1), request.getParamStr(2)));
        List<String> paramsStrList = request.getParamsStrList();
        DictKey dictKey = safeKey(paramsStrList.get(0));
        DictValue oldValue = db.getDict().putIfAbsent(dictKey, dictValue);
        if (null == oldValue) {
            return new IntegerRedisMessage(1);
        }
        Map<String, String> oldHash = oldValue.getHash();
        boolean isExists = null == oldHash.get(request.getParamStr(1));
        oldHash.putAll(dictValue.getHash());
        return new IntegerRedisMessage(isExists ? 1 : 0);
    }
}
