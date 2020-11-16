package com.daicy.redis;

import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FixedRedisMessagePool;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/10/20
 */
public class RedisConstants {

    public static final IntegerRedisMessage ZERO = new IntegerRedisMessage(0);

    public static final IntegerRedisMessage ONE = new IntegerRedisMessage(1);

    public static final SimpleStringRedisMessage OK =
            FixedRedisMessagePool.INSTANCE.getSimpleString("OK");

    public static final SimpleStringRedisMessage QUIT =
            FixedRedisMessagePool.INSTANCE.getSimpleString("QUIT");

    public static final ErrorRedisMessage NO_KEY =
            FixedRedisMessagePool.INSTANCE.getError("ERR no such key");


    public static final ErrorRedisMessage OUT_RANGE =
            FixedRedisMessagePool.INSTANCE.getError("ERR index out of range");

    public static final ErrorRedisMessage TYPE_ERROR =
            FixedRedisMessagePool.INSTANCE.getError("WRONGTYPE Operation against a key holding the wrong kind of value");
}
