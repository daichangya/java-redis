/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.zset;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.Dict;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.client.utils.RedisMessageUtils;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.reverse;
import static java.util.stream.Collectors.toList;

@ReadOnly
@Command("zrevrange")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetReverseRangeCommand implements DBCommand {

    private static final String PARAM_WITHSCORES = "WITHSCORES";

    @Override
    public RedisMessage execute(RedisDb redisDb, Request request) {
        try {
            Dict db = redisDb.getDict();
            Set<Entry<Double, String>> set = db.getSortedSet(request.getParamStr(0));

            int from = Integer.parseInt(request.getParamStr(2));
            if (from < 0) {
                from = set.size() + from;
            }
            int to = Integer.parseInt(request.getParamStr(1));
            if (to < 0) {
                to = set.size() + to;
            }

            List<String> result = emptyList();
            if (from <= to) {
                if (request.getParamsStrList().size() == 4 && request.getParamStr(3).equalsIgnoreCase(PARAM_WITHSCORES)) {
                    result = set.stream().skip(from).limit((to - from) + 1l)
                            .flatMap(item -> Stream.of(item.getValue(), String.valueOf(item.getKey()))).collect(toList());
                } else {
                    result = set.stream().skip(from).limit((to - from) + 1l)
                            .map(Entry::getValue).collect(toList());
                }
            }
            reverse(result);

            return RedisMessageUtils.toRedisMessage(result);
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("ERR value is not an integer or out of range");
        }
    }
}
