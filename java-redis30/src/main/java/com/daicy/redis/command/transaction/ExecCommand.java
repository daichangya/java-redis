/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.transaction;


import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.RedisClientSession;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.TxIgnore;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.command.RedisCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.daicy.redis.RedisConstants.REDIS_MULTI;

@Command("exec")
@TxIgnore
public class ExecCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    RedisClientSession session = request.getClientSession();

    // 客户端没有执行事务
    if ((session.getFlags() & REDIS_MULTI) == 0) {
      return new ErrorRedisMessage("EXEC without MULTI");
    }
    MultiState state = session.getMultiState();
    DefaultRedisServerContext redisServerContext = request.getServerContext();
    List<RedisMessage> responses = new ArrayList<>();
    for (Iterator<Request> it = state.iterator(); it.hasNext(); ) {
      Request queuedRequest = it.next();
      responses.add(executeCommand(redisServerContext,queuedRequest));
    }
    return new MultiBulkRedisMessage(responses);
  }

  private RedisMessage executeCommand(DefaultRedisServerContext redisServerContext,Request queuedRequest) {
    RedisCommand command = redisServerContext.getRedisCommand(queuedRequest.getCommand());
    return command.execute(queuedRequest);
  }

}
