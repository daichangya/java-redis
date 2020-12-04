/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.scripting;

import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.DefaultRequest;
import com.daicy.redis.RedisClientSession;
import com.daicy.redis.Request;
import com.daicy.redis.command.RedisCommand;
import com.daicy.redis.protocal.RedisMessage;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public class RedisLibrary {

  private final DefaultRedisServerContext context;
  private final RedisClientSession session;

  public RedisLibrary(DefaultRedisServerContext context, RedisClientSession session) {
    this.context = requireNonNull(context);
    this.session = requireNonNull(session);
  }

  public RedisMessage call(String commandName, String... params) {
    return getCommand(commandName).execute(createRequest(commandName, params));
  }

  private RedisCommand getCommand(String commandName) {
    return context.getRedisCommand(commandName.toString());
  }

  private Request createRequest(String commandName, String... params) {
    return new DefaultRequest(commandName, Arrays.asList(params),session,context);
  }
}
