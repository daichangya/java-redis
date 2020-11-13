/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.list;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.redis.RedisMessage;

import java.util.ArrayList;
import java.util.List;

@Command("lset")
@ParamLength(3)
@ParamType(DataType.LIST)
public class ListSetCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    try {
      int index = Integer.parseInt(request.getParam(1).toString());
      db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_LIST,
          (oldValue, newValue) -> {
            ImmutableList<String> oldList = oldValue.getList();
            // TODO: use Array
            List<String> array = new ArrayList<>(oldList.toList());
            array.set(index > -1 ? index : array.size() + index, request.getParam(2));
            return list(array);
          });
      return status("OK");
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    } catch (IndexOutOfBoundsException e) {
      return error("ERR index out of range");
    }
  }
}
