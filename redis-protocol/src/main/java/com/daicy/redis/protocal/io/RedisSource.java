/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.protocal.io;

public interface RedisSource {

  int available();
  String readLine(); 
  String readString(int length);
}
