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

//    #define REDIS_REPL_WAIT_BGSAVE_START 6 /* We need to produce a new RDB file. */
//            #define REDIS_REPL_WAIT_BGSAVE_END 7 /* Waiting RDB file creation to finish. */
//            #define REDIS_REPL_SEND_BULK 8 /* Sending RDB file to slave. */
//            #define REDIS_REPL_ONLINE 9 /* RDB file transmitted, sending just updates. */

    public static int REDIS_REPL_WAIT_BGSAVE_START = 6;

    public static int REDIS_REPL_WAIT_BGSAVE_END = 7;

    public static int REDIS_REPL_SEND_BULK = 8;

    public static int REDIS_REPL_ONLINE = 0;


}
