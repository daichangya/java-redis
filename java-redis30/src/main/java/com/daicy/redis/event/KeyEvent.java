/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.event;


import static java.lang.String.format;

class KeyEvent extends Event {
  
  private static final String CHANNEL_PATTERN = "__keyevent__@%d__:%s";

  public KeyEvent(String command, String key, int schema) {
    super(command, key, schema);
  }
  
  @Override
  public String getValue() {
    return getCommand();
  }
  
  @Override
  public String getChannel() {
    return format(CHANNEL_PATTERN, getSchema(), getKey());
  }
}
