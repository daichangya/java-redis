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
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.redis.RedisMessage;

@ReadOnly
@Command("lindex")
@ParamLength(2)
@ParamType(DataType.LIST)
public class ListIndexCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    try {
      ImmutableList<String> list = db.getList(request.getParam(0));

      int index = Integer.parseInt(request.getParam(1).toString());
      if (index < 0) {
        index = list.size() + index;
      }

      // TODO: fix asArray
      return string(list.asArray().get(index));
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    } catch (IndexOutOfBoundsException e) {
      return nullString();
    }
  }
}
