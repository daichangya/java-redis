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
import com.daicy.redis.client.utils.RedisMessageUtils;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.CowSortedSet;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import static com.daicy.redis.storage.DictValue.score;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

@ReadOnly
@Command("zrangebyscore")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetRangeByScoreCommand implements DBCommand {

    private static final String EXCLUSIVE = "(";
    private static final String MINUS_INFINITY = "-inf";
    private static final String INIFITY = "+inf";
    private static final String PARAM_WITHSCORES = "WITHSCORES";
    private static final String PARAM_LIMIT = "LIMIT";

    @Override
    public RedisMessage execute(RedisDb redisDb, Request request) {
        try {
            CowSortedSet set = redisDb.lookupKeyOrDefault(request.getParamStr(0),
                    DictValue.EMPTY_ZSET).getSortedSet();
            if(set.size() == 0){
                return new MultiBulkRedisMessage(null);
            }
            float from = parseRange(request.getParamStr(1));
            float to = parseRange(request.getParamStr(2));

            Options options = parseOptions(request);

            Set<Entry<Double, String>> range = set.subSet(
                    score(from, null), inclusive(request.getParamStr(1)),
                    score(to, null), inclusive(request.getParamStr(2)));

            List<String> result = Lists.newArrayList();
            if (from <= to) {
                if (options.withScores) {
                    result = range.stream().flatMap(
                            entry -> Stream.of(entry.getValue(), String.valueOf(entry.getKey()))).collect(toList());
                } else {
                    result = range.stream().map(Entry::getValue).collect(toList());
                }

                if (options.withLimit) {
                    result = result.stream().skip(options.offset).limit(options.count).collect(toList());
                }
            }

            return RedisMessageUtils.toRedisMessage(result);
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("ERR value is not an float or out of range");
        }
    }

    private Options parseOptions(Request request) {
        Options options = new Options();
        for (int i = 3; i < request.getLength(); i++) {
            String param = request.getParamStr(i);
            if (param.equalsIgnoreCase(PARAM_LIMIT)) {
                options.withLimit = true;
                options.offset = parseInt(request.getParamStr(++i));
                options.count = parseInt(request.getParamStr(++i));
            } else if (param.equalsIgnoreCase(PARAM_WITHSCORES)) {
                options.withScores = true;
            }
        }
        return options;
    }

    private boolean inclusive(String param) {
        return !param.startsWith(EXCLUSIVE);
    }

    private float parseRange(String param) {
        switch (param) {
            case INIFITY:
                return Float.MAX_VALUE;
            case MINUS_INFINITY:
                return Float.MIN_VALUE;
            default:
                if (param.startsWith(EXCLUSIVE)) {
                    return Float.parseFloat(param.substring(1));
                }
                return Float.parseFloat(param);
        }
    }


    private static class Options {
        private boolean withScores;
        private boolean withLimit;
        private int offset;
        private int count;
    }
}
