package com.daicy.redis;

import io.netty.handler.codec.redis.FixedRedisMessagePool;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/10/20
 */
public class RedisConstants {
    public static final SimpleStringRedisMessage OK =
            FixedRedisMessagePool.INSTANCE.getSimpleString("OK");

    public static final SimpleStringRedisMessage QUIT =
            FixedRedisMessagePool.INSTANCE.getSimpleString("QUIT");
}
