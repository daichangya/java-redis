/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.event;


import static java.lang.String.format;

class KeySpace extends Event {
  
  private static final String CHANNEL_PATTERN = "__keyspace__@%d__:%s";

  public KeySpace(String command, String key, int schema) {
    super(command, key, schema);
  }
  
  @Override
  public String getValue() {
    return getKey();
  }
  
  @Override
  public String getChannel() {
    return format(CHANNEL_PATTERN, getSchema(), getCommand());
  }
}
