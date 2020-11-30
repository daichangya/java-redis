/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.hash;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;

import java.util.Map;


@ReadOnly
@Command("hlen")
@ParamLength(1)
@ParamType(DataType.HASH)
public class HashLengthCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    Map<String, String> dictValueHash =
            db.lookupKeyOrDefault(request.getParamStr(0), DictValue.EMPTY_HASH).getHash();
    return new IntegerRedisMessage(dictValueHash.size());
  }
}
