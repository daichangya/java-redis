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
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.*;

import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;

import static com.daicy.redis.storage.DictKey.safeKey;
import static com.daicy.redis.storage.DictValue.score;
import static com.daicy.redis.storage.DictValue.zset;
import static java.lang.Float.parseFloat;
import static java.util.stream.Collectors.toList;

@Command("zadd")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetAddCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb redisDb, Request request) {
        try {
            Dict db = redisDb.getDict();
            CowSortedSet initial = db.getSortedSet(request.getParamStr(0));
            DictValue result = db.merge(redisDb,safeKey(request.getParamStr(0)), parseInput(request),
                    (oldValue, newValue) -> {
                        Set<Map.Entry<Double, String>> merge = new CowSortedSet();
                        merge.addAll(oldValue.getSortedSet());
                        merge.addAll(newValue.getSortedSet());
                        return zset(merge);
                    });
            return new IntegerRedisMessage(changed(initial, result.getSortedSet()));
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("ERR value is not a valid float");
        }
    }

    private int changed(Set<Entry<Double, String>> input, Set<Entry<Double, String>> result) {
        return result.size() - input.size();
    }

    private DictValue parseInput(Request request) {
        Set<Entry<Double, String>> set = new CowSortedSet();
        String score = null;
        List<String> paramsStrList = request.getParamsStrList();
        for (String string : paramsStrList.subList(1, paramsStrList.size()).stream().collect(toList())) {
            if (score != null) {
                set.add(score(parseFloat(score.toString()), string));
                score = null;
            } else {
                score = string;
            }
        }
        return zset(set);
    }

}
