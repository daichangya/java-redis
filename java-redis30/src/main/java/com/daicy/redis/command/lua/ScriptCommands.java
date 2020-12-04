/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.lua;


import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.SHA1;

import static com.daicy.redis.protocal.RedisMessageConstants.*;


@ParamLength(1)
@Command("script")
public class ScriptCommands implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    String command = request.getParamStr(0);
    switch (command) {
      case "load":
        return load(request);
      case "exists":
        return exists(request);
      case "flush":
        return flush(request);
      default:
        return unknownCommand(request);
    }
  }

  private RedisMessage unknownCommand(Request request) {
    return new ErrorRedisMessage("Unknown SCRIPT subcommand: " + request.getParamStr(0));
  }

  private RedisMessage load(Request request) {
    String script = request.getParamStr(1);
    String sha1 = digest(script);
    DefaultRedisServerContext defaultRedisServerContext = request.getServerContext();
    defaultRedisServerContext.getLuaScripts().put(sha1,script);
    return RedisMessage.string(sha1);
  }

  private RedisMessage exists(Request request) {
    DefaultRedisServerContext defaultRedisServerContext = request.getServerContext();
    if(defaultRedisServerContext.getLuaScripts().containsKey(request.getParamStr(1))){
      return ONE;
    }else {
      return ZERO;
    }
  }

  private RedisMessage flush(Request request) {
    DefaultRedisServerContext defaultRedisServerContext = request.getServerContext();
    defaultRedisServerContext.getLuaScripts().clear();
    return OK;
  }

  private String digest(String script) {
    return SHA1.encode(script);
  }

}
