/*
 * Copyright (c) 2016-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.bitset;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;

import java.util.BitSet;

import static com.daicy.redis.storage.DictKey.safeKey;
import static com.daicy.redis.storage.DictValue.bitset;


@Command("bitcount")
@ParamLength(1)
@ParamType(DataType.STRING)
public class BitCountCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        DictValue value = db.getDict().getOrDefault(safeKey(request.getParamStr(0)), bitset());
        BitSet bitSet = BitSet.valueOf(value.getString().getBytes());
        return new IntegerRedisMessage(bitSet.cardinality());
    }
}
