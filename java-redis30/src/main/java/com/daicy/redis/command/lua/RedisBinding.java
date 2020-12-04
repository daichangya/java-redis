/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.lua;

import com.daicy.redis.protocal.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class RedisBinding extends VarArgFunction {

  private final RedisLibrary redis;

  public RedisBinding(RedisLibrary redis) {
    this.redis = requireNonNull(redis);
  }

  @Override
  public Varargs invoke(Varargs args) {
    return convert(redis.call(readCommand(args), readArguments(args)));
  }

  private String[] readArguments(Varargs args) {
    List<String> params = new ArrayList<>();
    if (args.narg() > 1) {
      for (int i = 1; i < args.narg(); i++) {
        params.add(toSafeString(args.checkstring(i + 1)));
      }
    }
    return params.toArray(new String[0]);
  }

  private String readCommand(Varargs args) {
    return toSafeString(args.checkstring(1));
  }

  private String toSafeString(LuaString value)
  {
    return new String(value.m_bytes);
  }

  private LuaValue convert(RedisMessage token) {
    if(token instanceof BulkByteRedisMessage){
      return toLuaString((BulkByteRedisMessage) token);
    }else if(token instanceof StatusRedisMessage){
      return toLuaString((StatusRedisMessage) token);
    }else if(token instanceof MultiBulkRedisMessage){
      return toLuaTable((MultiBulkRedisMessage) token);
    }else if(token instanceof IntegerRedisMessage){
      return toLuaNumber((IntegerRedisMessage) token);
    }else {
      return toLuaString((UnknownRedisMessage) token);
    }
  }

  private LuaValue toLuaNumber(IntegerRedisMessage value) {
    Integer integer = value.data();
    if (integer == null) {
      return LuaValue.NIL;
    }
    return LuaInteger.valueOf(integer);
  }

  private LuaTable toLuaTable(MultiBulkRedisMessage value) {
    LuaTable table = LuaValue.tableOf();
    int i = 0;
    for (RedisMessage token : value.data()) {
      table.set(++i, convert(token));
    }
    return table;
  }

  private LuaValue toLuaString(BulkByteRedisMessage value) {
    String string = value.toString();
    if (string == null) {
      return LuaValue.NIL;
    }
    return LuaString.valueOf(string.getBytes());
  }

  private LuaValue toLuaString(StatusRedisMessage value) {
    String string = value.data();
    if (string == null) {
      return LuaValue.NIL;
    }
    return LuaString.valueOf(string);
  }

  private LuaValue toLuaString(UnknownRedisMessage value) {
    String string = value.data();
    if (string == null) {
      return LuaValue.NIL;
    }
    return LuaString.valueOf(string.getBytes());
  }
}
