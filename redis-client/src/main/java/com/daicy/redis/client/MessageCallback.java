/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.client;

import com.daicy.redis.protocal.RedisMessage;
import com.daicy.remoting.transport.netty4.client.ClientCallback;

public interface MessageCallback extends ClientCallback<RedisMessage> {
  void onMessage(RedisMessage redisMessage);
}
