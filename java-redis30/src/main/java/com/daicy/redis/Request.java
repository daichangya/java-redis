/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis;


import io.netty.handler.codec.redis.ArrayRedisMessage;
import com.daicy.redis.protocal.Reply;
import io.netty.handler.codec.redis.RedisMessage;

import java.util.List;


/**
 * @author daichangya
 * https://github.com/redis/redis/blob/3.0/src/redis.h
 * redisClient
 */
public interface Request {
    String getCommand();

    ArrayRedisMessage getParams();

    RedisMessage getParam(int i);

    List<String> getParamsStrList();

    String getParamStr(int i);

    int getLength();

    RedisClientSession getClientSession();

    boolean isEmpty();

    boolean isExit();
}
