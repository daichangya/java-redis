/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;

import static java.util.Objects.requireNonNull;

public enum DataType {
  LONG("long"),
  STRING("string"),
  LIST("list"),
  SET("set"),
  ZSET("zset"),
  HASH("hash"),
  NONE("none");

  private final String text;

  DataType(String text) {
    this.text = requireNonNull(text);
  }

  public String text() {
    return text;
  }
}
