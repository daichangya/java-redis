/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.DictUtils;
import com.google.common.collect.UnmodifiableIterator;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

import static io.netty.handler.codec.redis.FullBulkStringRedisMessage.NULL_INSTANCE;

@Command("randomkey")
public class RandomKeyCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        UnmodifiableIterator<DictKey> dictKeys = db.getDict().keySet().iterator();
        while (dictKeys.hasNext()) {
            DictKey dictKey = dictKeys.next();
            if (!DictUtils.isExpired(db, dictKey)) {
                return new SimpleStringRedisMessage(dictKey.getValue());
            }
        }
        return NULL_INSTANCE;
    }
}
