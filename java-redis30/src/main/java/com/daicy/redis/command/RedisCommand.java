/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


import com.daicy.redis.Request;
import io.netty.handler.codec.redis.RedisMessage;

@FunctionalInterface
public interface RedisCommand {
  RedisMessage execute(Request request);
}
