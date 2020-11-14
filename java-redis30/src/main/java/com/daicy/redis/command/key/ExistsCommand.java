/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.DictUtils;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

import java.util.List;

@ReadOnly
@Command("exists")
@ParamLength(1)
public class ExistsCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    List<DictValue> result = DictUtils.getValues(db,request.getParamsStrList());
    return new IntegerRedisMessage(result.size());
  }
}
