/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.pubsub;

import com.daicy.redis.Request;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.Lists;

import java.util.List;

public abstract class SubscriptionSupport implements BaseSubscriptionSupport,DBCommand{

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    List<String> params = request.getParamsStrList();
    List<RedisMessage> resultMessage = Lists.newArrayList();
    for (int j = 0; j < params.size(); j++) {
      resultMessage.add(pubsubSubscribeChannel(request, params.get(j)));
    }
    return new MultiBulkRedisMessage(resultMessage);
  }

  public abstract RedisMessage pubsubSubscribeChannel(Request request, String channel);



}
