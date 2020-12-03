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
//    #define REDIS_REPL_WAIT_BGSAVE_END 7 /* Waiting RDB file creation to finish. */
//    #define REDIS_REPL_SEND_BULK 8 /* Sending RDB file to slave. */
//    #define REDIS_REPL_ONLINE 9 /* RDB file transmitted, sending just updates. */

    public static int REDIS_REPL_WAIT_BGSAVE_START = 6;

    public static int REDIS_REPL_WAIT_BGSAVE_END = 7;

    public static int REDIS_REPL_SEND_BULK = 8;

    public static int REDIS_REPL_ONLINE = 0;


//        /* Slave replication state - from the point of view of the slave. */
//        #define REDIS_REPL_NONE 0 /* No active replication */
//        #define REDIS_REPL_CONNECT 1 /* Must connect to master */
//        #define REDIS_REPL_CONNECTING 2 /* Connecting to master */
//        #define REDIS_REPL_RECEIVE_PONG 3 /* Wait for PING reply */
//        #define REDIS_REPL_TRANSFER 4 /* Receiving .rdb from master */
//        #define REDIS_REPL_CONNECTED 5 /* Connected to master */

    public static int REDIS_REPL_NONE = 0;

    public static int REDIS_REPL_CONNECT = 1;

    public static int REDIS_REPL_CONNECTING = 2;

    public static int REDIS_REPL_RECEIVE_PONG = 3;

    public static int REDIS_REPL_TRANSFER = 4;

    public static int REDIS_REPL_CONNECTED = 5;

}
