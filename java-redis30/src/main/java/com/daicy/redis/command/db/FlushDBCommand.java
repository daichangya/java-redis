/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.daicy.redis.command.db;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.Dict;
import com.daicy.redis.storage.RedisDb;
import io.netty.handler.codec.redis.RedisMessage;

import static com.daicy.redis.RedisConstants.OK;

@Command("flushdb")
public class FlushDBCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    db.getDict().clear();
    db.getExpires().clear();
    return OK;
  }
}
