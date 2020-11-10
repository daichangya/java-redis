/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;

import com.daicy.redis.Request;
import com.daicy.redis.database.Database;
import io.netty.handler.codec.redis.RedisMessage;


@FunctionalInterface
public interface DBCommand {
  RedisMessage execute(Database db, Request request);
}
