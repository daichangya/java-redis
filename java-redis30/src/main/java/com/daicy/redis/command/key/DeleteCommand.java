/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.IntegerReply;
import com.daicy.redis.storage.Dict;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import com.daicy.redis.protocal.Reply;

@Command("del")
@ParamLength(1)
public class DeleteCommand implements DBCommand {

    @Override
    public Reply execute(RedisDb db, Request request) {
        int removed = 0;
        for (String key : request.getParamsStrList()) {
            DictValue value = db.getDict().remove(new DictKey(key));
            if (value != null) {
                removed += 1;
            }
        }
        return new IntegerReply(removed);
    }
}
