/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.GlobPattern;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.DictUtils;

import java.util.List;
import java.util.stream.Collectors;

@ReadOnly
@Command("keys")
@ParamLength(1)
public class KeysCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        GlobPattern pattern = createPattern(request.getParamStr(0));
        List<RedisMessage> keys = db.getDict().keySet().stream()
                .filter(dictKey -> pattern.match(dictKey.getValue()))
                .filter(dictKey -> filterExpired(db, dictKey))
                .map(dictKey -> dictKey.getValue())
                .map(dictKey -> new BulkRedisMessage(dictKey))
                .collect(Collectors.toList());
        return new MultiBulkRedisMessage(keys);
    }

    private GlobPattern createPattern(String param) {
        return new GlobPattern(param);
    }

    private boolean filterExpired(RedisDb db, DictKey dictKey) {
        return !DictUtils.isExpired(db, dictKey);
    }
}
