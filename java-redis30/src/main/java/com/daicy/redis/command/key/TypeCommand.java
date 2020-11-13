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
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

import static com.daicy.redis.storage.DictKey.safeKey;

@ReadOnly
@Command("type")
@ParamLength(1)
public class TypeCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    DictValue value = db.getDict().get(safeKey(request.getParamStr(0)));
    if (value != null) {
      return new SimpleStringRedisMessage(value.getType().text());
    } else {
      return new SimpleStringRedisMessage(DataType.NONE.text());
    }
  }
}