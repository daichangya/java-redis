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

//    public static int  REDIS_REPL_WAIT_BGSAVE_START 6 /* We need to produce a new RDB file. */
//    public static int  REDIS_REPL_WAIT_BGSAVE_END 7 /* Waiting RDB file creation to finish. */
//    public static int  REDIS_REPL_SEND_BULK 8 /* Sending RDB file to slave. */
//    public static int  REDIS_REPL_ONLINE 9 /* RDB file transmitted, sending just updates. */

    public static int REDIS_REPL_WAIT_BGSAVE_START = 6;

    public static int REDIS_REPL_WAIT_BGSAVE_END = 7;

    public static int REDIS_REPL_SEND_BULK = 8;

    public static int REDIS_REPL_ONLINE = 0;


//        /* Slave replication state - from the point of view of the slave. */
//        public static int  REDIS_REPL_NONE 0 /* No active replication */
//        public static int  REDIS_REPL_CONNECT 1 /* Must connect to master */
//        public static int  REDIS_REPL_CONNECTING 2 /* Connecting to master */
//        public static int  REDIS_REPL_RECEIVE_PONG 3 /* Wait for PING reply */
//        public static int  REDIS_REPL_TRANSFER 4 /* Receiving .rdb from master */
//        public static int  REDIS_REPL_CONNECTED 5 /* Connected to master */

    public static int REDIS_REPL_NONE = 0;

    public static int REDIS_REPL_CONNECT = 1;

    public static int REDIS_REPL_CONNECTING = 2;

    public static int REDIS_REPL_RECEIVE_PONG = 3;

    public static int REDIS_REPL_TRANSFER = 4;

    public static int REDIS_REPL_CONNECTED = 5;


    /* Client flags */
    public static int REDIS_SLAVE = (1 << 0);   /* This client is a slave server */
    public static int REDIS_MASTER = (1 << 1);  /* This client is a master server */
    public static int REDIS_MONITOR = (1 << 2); /* This client is a slave monitor, see MONITOR */
    public static int REDIS_MULTI = (1 << 3);   /* This client is in a MULTI context */
    public static int REDIS_BLOCKED = (1 << 4); /* The client is waiting in a blocking operation */
    public static int REDIS_DIRTY_CAS = (1 << 5); /* Watched keys modified. EXEC will fail. */
    public static int REDIS_CLOSE_AFTER_REPLY = (1 << 6); /* Close after writing entire reply. */
    public static int REDIS_UNBLOCKED = (1 << 7); /* This client was unblocked and is stored in
                                  server.unblocked_clients */
    public static int REDIS_LUA_CLIENT = (1 << 8); /* This is a non connected client used by Lua */
    public static int REDIS_ASKING = (1 << 9);     /* Client issued the ASKING command */
    public static int REDIS_CLOSE_ASAP = (1 << 10);/* Close this client ASAP */
    public static int REDIS_UNIX_SOCKET = (1 << 11); /* Client connected via Unix domain socket */
    public static int REDIS_DIRTY_EXEC = (1 << 12);  /* EXEC will fail for errors while queueing */
    public static int REDIS_MASTER_FORCE_REPLY = (1 << 13);  /* Queue replies even if is master */
    public static int REDIS_FORCE_AOF = (1 << 14);   /* Force AOF propagation of current cmd. */
    public static int REDIS_FORCE_REPL = (1 << 15);  /* Force replication of current cmd. */
    public static int REDIS_PRE_PSYNC = (1 << 16);   /* Instance don't understand PSYNC. */
    public static int REDIS_READONLY = (1 << 17);    /* Cluster client is in read-only state. */


}
