/*
 * Copyright (c) 2016-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;

public class DefaultCommandWrapperFactory implements CommandWrapperFactory {
  @Override
  public RedisCommand wrap(Object command) {
    if (command instanceof RedisCommand) {
      return new CommandWrapper((RedisCommand) command);
    }
    throw new IllegalArgumentException("must implements command interface");
  }
}
