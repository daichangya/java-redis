/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.ParamLength;
import com.google.common.base.Preconditions;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;


public class CommandWrapper implements RedisCommand {

  private int params;

  private final RedisCommand command;

  public CommandWrapper(RedisCommand command) {
    this.command = Preconditions.checkNotNull(command);
    ParamLength length = command.getClass().getAnnotation(ParamLength.class);
    if (length != null) {
      this.params = length.value();
    }
  }

  @Override
  public RedisMessage execute(Request request) {
    if (request.getLength() < params) {
      return new ErrorRedisMessage("ERR wrong number of arguments for '" + request.getCommand() + "' command");
    }
    return command.execute(request);
  }
}
