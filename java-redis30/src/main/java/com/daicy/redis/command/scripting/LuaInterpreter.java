/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.scripting;

import com.daicy.redis.Request;
import com.daicy.redis.protocal.*;
import org.luaj.vm2.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

import static com.daicy.redis.protocal.RedisMessageConstants.NULL;
import static com.daicy.redis.protocal.RedisMessageConstants.ONE;
import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;

public final class LuaInterpreter {

  private final RedisBinding redis;

  protected LuaInterpreter(RedisBinding binding) {
    this.redis = requireNonNull(binding);
  }

  public static LuaInterpreter buildFor(Request request) {
    return new LuaInterpreter(createBinding(request));
  }

  public RedisMessage execute(String script, List<String> keys, List<String> params) {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("luaj");
      engine.put("redis", createBinding(redis));
      engine.put("KEYS", toArray(keys));
      engine.put("ARGV", toArray(params));
      return convert(engine.eval(script.toString()));
    } catch (ScriptException e) {
      return new ErrorRedisMessage(e.getMessage());
    }
  }

  private LuaValue createBinding(RedisBinding redis) {
    LuaTable binding = LuaTable.tableOf();
    binding.set("call", redis);
    return binding;
  }

  private RedisMessage convert(Object result) {
    if(result instanceof LuaTable){
      return convertLuaTable((LuaTable) result);
    }else if(result instanceof LuaNumber){
      return convertLuaNumber((LuaNumber) result);
    }else if(result instanceof LuaBoolean){
      return convertLuaBoolean((LuaBoolean) result);
    }else if(result instanceof LuaString){
      return convertLuaString((LuaString) result);
    }else if(result instanceof Number){
      return convertNumber((Number) result);
    }else if(result instanceof String){
      return convertString((String) result);
    }else if(result instanceof Boolean){
      return convertBoolean((Boolean) result);
    }else {
      return convertUnknown(result);
    }
  }

  private RedisMessage convertLuaTable(LuaTable value) {
    List<RedisMessage> tokens = new ArrayList<>();
    for (LuaValue key : value.keys()) {
      tokens.add(convert(value.get(key)));
    }
    return new MultiBulkRedisMessage(tokens);
  }

  private RedisMessage convertLuaNumber(LuaNumber value) {
    return new IntegerRedisMessage(value.toint());
  }

  private RedisMessage convertLuaString(LuaString value) {
    return new BulkRedisMessage(value.tojstring());
  }

  private RedisMessage convertLuaBoolean(LuaBoolean value) {
    return value.toboolean() ? ONE : NULL;
  }

  private RedisMessage convertNumber(Number number) {
    return new IntegerRedisMessage(number.intValue());
  }

  private RedisMessage convertString(String string) {
    return new BulkRedisMessage(string);
  }

  private RedisMessage convertBoolean(Boolean value) {
    return value ?ONE : NULL;
  }

  private RedisMessage convertUnknown(Object value) {
    return value != null ? new BulkRedisMessage(valueOf(value)) : NULL;
  }

  private Object[] toArray(List<String> keys) {
    return keys.stream().map(String::toString).toArray(String[]::new);
  }

  private static RedisBinding createBinding(Request request) {
    return new RedisBinding(createLibrary(request));
  }

  private static RedisLibrary createLibrary(Request request) {
    return new RedisLibrary(request.getServerContext(), request.getClientSession());
  }
}
