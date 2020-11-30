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
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.daicy.redis.protocal.RedisMessageConstants.OK;

@Command("hmset")
@ParamLength(3)
@ParamType(DataType.HASH)
public class HashMultiSetCommand implements DBCommand {


    @Override
    public RedisMessage execute(RedisDb redisDb, Request request) {
        Map<String,String> newValue = Maps.newHashMap();
        for (int paramNumber = 1; paramNumber < request.getParamsStrList().size(); paramNumber += 2) {
            String mapKey = request.getParamStr(paramNumber);
            String mapVal = request.getParamStr(paramNumber + 1);
            newValue.put(mapKey,mapVal);
        }
        DictValue oldValue = redisDb.getDict().putIfAbsent(redisDb,DictKey.safeKey(request.getParamStr(0)),
                DictValue.hash(newValue));
        if(null == oldValue){
            return OK;
        }
        oldValue.getHash().putAll(newValue);
        return OK;
    }
}
