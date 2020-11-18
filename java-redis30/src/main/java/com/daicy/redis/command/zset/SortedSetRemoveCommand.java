/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.zset;


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
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static com.daicy.redis.protocal.RedisMessageConstants.ZERO;

@Command("zrem")
@ParamLength(2)
@ParamType(DataType.ZSET)
public class SortedSetRemoveCommand implements DBCommand {


    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        List<String> paramsStrList = request.getParamsStrList();
        List<String> removeStrs = paramsStrList.subList(1, paramsStrList.size());
        Set<Entry<Double, String>> stringSet = db.getDict().getSortedSet(request.getParamStr(0));
        if (CollectionUtils.isEmpty(stringSet)) {
            return ZERO;
        }
        int result = 0;
        for (int i = 0; i < removeStrs.size(); i++) {
            if (stringSet.remove(DictValue.score(0, removeStrs.get(i)))) {
                result++;
            }
        }
        return new IntegerRedisMessage(result);
    }
}
