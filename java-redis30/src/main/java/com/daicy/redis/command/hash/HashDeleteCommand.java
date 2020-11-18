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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.daicy.redis.protocal.RedisMessageConstants.ZERO;
import static com.daicy.redis.storage.DictKey.safeKey;

@Command("hdel")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashDeleteCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        Map<String, String> dictValueHash = db.getDict().getHash(request.getParamStr(0));
        if (MapUtils.isEmpty(dictValueHash)) {
            return ZERO;
        }
        List<String> paramsStrList = request.getParamsStrList();
        List<String> removeKeys = paramsStrList.subList(1, paramsStrList.size());
        AtomicInteger result = new AtomicInteger();
        removeKeys.forEach(removeKey -> {
            if (StringUtils.isNotEmpty(dictValueHash.remove(removeKey))) {
                result.getAndIncrement();
            }
        });

        return new IntegerRedisMessage(result.get());
    }
}
