/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.daicy.redis.command.string;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.DictUtils;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

import static com.daicy.redis.RedisConstants.TYPE_ERROR;
import static io.netty.handler.codec.redis.FullBulkStringRedisMessage.NULL_INSTANCE;

@Command("get")
@ParamLength(1)
public class GetCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        DictValue dictValue = DictUtils.getValue(db, request.getParamStr(0));
        if (null == dictValue) {
            return NULL_INSTANCE;
        }
        if (!dictValue.getType().equals(DataType.STRING)) {
            return TYPE_ERROR;
        }
        return new SimpleStringRedisMessage(dictValue.getString());
    }
}