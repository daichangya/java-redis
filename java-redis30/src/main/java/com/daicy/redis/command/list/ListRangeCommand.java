/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.list;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.redis.RedisMessage;

@ReadOnly
@Command("lrange")
@ParamLength(3)
@ParamType(DataType.LIST)
public class ListRangeCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    try {
      DictValue value = db.getDict().getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_LIST);
      ImmutableList<String> list = value.getList();

      int from = Integer.parseInt(request.getParam(1).toString());
      if (from < 0) {
        from = list.size() + from;
      }
      int to = Integer.parseInt(request.getParam(2).toString());
      if (to < 0) {
        to = list.size() + to;
      }

      int min = Math.min(from, to);
      int max = Math.max(from, to);

      // TODO: use Array
      ImmutableList<String> result = ImmutableList.from(list.stream().skip(min).limit((max - min) + 1));

      return convert(result);
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    }
  }
}
