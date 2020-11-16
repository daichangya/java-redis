/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.daicy.redis.command.db;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.protocal.Reply;

import static com.daicy.redis.protocal.ReplyConstants.OK;


@Command("flushdb")
public class FlushDBCommand implements DBCommand {

  @Override
  public Reply execute(RedisDb db, Request request) {
    db.getDict().clear();
    db.getExpires().clear();
    return OK;
  }
}
