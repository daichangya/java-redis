/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;

import static java.util.Objects.requireNonNull;

public enum DataType {
  STRING("string",0),
  LIST("list",1),
  SET("set",2),
  ZSET("zset",3),
  HASH("hash",4),
  LONG("long",5),
  NONE("none",6);

  private final String text;

  private final int value;

  DataType(String text,int value) {
    this.text = requireNonNull(text);
    this.value = value;
  }

  public String text() {
    return text;
  }

  public int getValue(){
    return value;
  }
}
