/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis;


import java.util.List;


/**
 * @author daichangya
 * https://github.com/redis/redis/blob/3.0/src/redis.h
 * redisClient
 */
public interface Request {
    String getCommand();

    List<String> getParamsStrList();

    String getParamStr(int i);

    int getLength();

    RedisClientSession getClientSession();

    DefaultRedisServerContext getServerContext();

    boolean isEmpty();

    boolean isExit();
}
