/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;

@Command("echo")
@ParamLength(1)
public class EchoCommand implements RedisCommand {

  @Override
  public RedisMessage execute(Request request) {
    return new BulkRedisMessage(request.getParamStr(0));
  }
}
