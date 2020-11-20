/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis;


import com.daicy.redis.protocal.RedisMessage;

public interface ClientCallback {
  void onConnect();
  void onDisconnect();
  void onMessage(RedisMessage token);
}
