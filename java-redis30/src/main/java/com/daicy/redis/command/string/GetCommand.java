/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.daicy.redis.command.string;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import org.apache.commons.lang3.tuple.Pair;

import static com.daicy.redis.protocal.RedisMessageConstants.NULL;

@ReadOnly
@Command("get")
@ParamLength(1)
@ParamType(DataType.STRING)
public class GetCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        Pair<DictValue, RedisMessage> value =
                db.lookupKeyOrReply(request.getParamStr(0),
                        DataType.STRING, NULL);
        return value.getRight() == null ?
                new BulkRedisMessage(value.getLeft().getString()) :
                value.getRight();
    }
}
