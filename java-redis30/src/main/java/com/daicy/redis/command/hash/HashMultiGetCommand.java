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
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import static com.daicy.redis.protocal.RedisMessageConstants.NULL;

@ReadOnly
@Command("hmget")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashMultiGetCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        Map<String, String> dictValueHash =
                db.lookupKeyOrDefault(request.getParamStr(0), DictValue.EMPTY_HASH).getHash();
        List<RedisMessage> result = Lists.newArrayList();
        for (int paramNumber = 1; paramNumber < request.getParamsStrList().size(); paramNumber++) {
            String oss = dictValueHash.get(request.getParamStr(paramNumber));
            result.add(null == oss ? NULL : new BulkRedisMessage(oss));
        }

        return new MultiBulkRedisMessage(result);
    }

}
