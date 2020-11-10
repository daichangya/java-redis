/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

@Command("time")
public class TimeCommand implements RedisCommand {

  private static final int SCALE = 1000;

  @Override
  public RedisMessage execute(Request request) {
    long currentTimeMillis = Clock.systemDefaultZone().millis();
    List<RedisMessage> redisMessageList = new ArrayList<>();
    redisMessageList.add(new SimpleStringRedisMessage(seconds(currentTimeMillis)));
    redisMessageList.add(new SimpleStringRedisMessage(microseconds(currentTimeMillis)));
    return new ArrayRedisMessage(redisMessageList);
  }

  private static String seconds(long currentTimeMillis) {
    return String.valueOf(currentTimeMillis / SCALE);
  }

  // XXX: Java doesn't have microsecond accuracy
  private static String microseconds(long currentTimeMillis) {
    return String.valueOf((currentTimeMillis % SCALE) * SCALE);
  }
}
