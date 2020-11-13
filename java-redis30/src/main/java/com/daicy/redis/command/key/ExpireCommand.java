/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.DictUtils;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

import static com.daicy.redis.RedisConstants.ONE;
import static com.daicy.redis.RedisConstants.ZERO;

@Command("expire")
@ParamLength(2)
public class ExpireCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        try {
            DictKey dictKey = new DictKey(request.getParamStr(0));
            DictValue dictValue = db.getDict().get(dictKey);
            if (null == dictValue) {
                return ZERO;
            }
            db.getExpires().put(dictKey, DictValue.toLong(
                    DictUtils.toInstantSs(parsetTtl(request.getParamStr(1))).toEpochMilli()));
            return ONE;
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("ERR value is not an integer or out of range");
        }
    }

    private long parsetTtl(String param) {
        return Long.parseLong(param);
    }
}
