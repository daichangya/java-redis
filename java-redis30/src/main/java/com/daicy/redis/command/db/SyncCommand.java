/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.daicy.redis.command.db;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;

import static com.daicy.redis.RedisConstants.REDIS_REPL_WAIT_BGSAVE_END;

@ReadOnly
@Command("sync")
public class SyncCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    if(request.getServerContext().getSlaves().contains(request.getClientSession())){
      return null;
    }
    if (!request.getServerContext().isRdbIng()) {
      request.getServerContext().exportRDBBg();
    }
    request.getClientSession().setReplstate(REDIS_REPL_WAIT_BGSAVE_END);
    request.getServerContext().getSlaves().add(request.getClientSession());
    return null;
  }
}
