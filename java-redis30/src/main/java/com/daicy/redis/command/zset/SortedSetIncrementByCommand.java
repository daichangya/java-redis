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
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.*;

import java.util.Map.Entry;

import static com.daicy.redis.storage.DictKey.safeKey;
import static com.daicy.redis.storage.DictValue.score;


@Command("zincrby")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetIncrementByCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb redisDb, Request request) {
        try {
            Dict db = redisDb.getDict();
            DictKey zkey = safeKey(request.getParamStr(0));
            String key = request.getParamStr(2);
            Double increment = Double.parseDouble(request.getParamStr(1));
            DictValue newValue = DictValue.zset(score(increment,key));
            DictValue oldValue = db.putIfAbsent(zkey,newValue);
            if(null == oldValue){
                return new BulkRedisMessage(String.valueOf(increment));
            }

            CowSortedSet oldValueSortedSet = oldValue.getSortedSet();
            Entry<Double, String> newEntry = merge(oldValueSortedSet, key, increment);
            oldValueSortedSet.remove(newEntry);
            oldValueSortedSet.add(newEntry);
            return new BulkRedisMessage(newEntry.getKey().toString());
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("ERR value is not an integer or out of range");
        }
    }

    private Entry<Double, String> merge(CowSortedSet set, String key, Double increment) {
        return score(set.score(key) + increment, key);
    }
}
