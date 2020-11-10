/*
 * Copyright (c) 2016-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


public class DBCommandWrapperFactory implements CommandWrapperFactory {
  @Override
  public RedisCommand wrap(Object command) {
    return new DBCommandWrapper(command);
  }
}
