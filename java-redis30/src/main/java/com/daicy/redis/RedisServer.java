package com.daicy.redis;

import com.daicy.redis.context.DBConfig;
import com.daicy.redis.handler.RedisServerInitializer;
import com.daicy.remoting.transport.netty4.Server;
import com.daicy.remoting.transport.netty4.ServerBuilder;

/**
 * Redis server
 */
public class RedisServer {
    private static Integer port = 6380;

    public static void main(String[] args) throws Exception {
        DefaultRedisServerContext redisServerContext = new DefaultRedisServerContext(DBConfig.builder().withPersistence().build());
        Server redisServer = ServerBuilder.forPort(port).setServerContext(redisServerContext)
                .channelInitializer(new RedisServerInitializer(redisServerContext)).build();
        Runtime.getRuntime().addShutdownHook(new Thread(redisServer::shutdown));
        redisServer.init();
        redisServer.start()
                .thenAccept(ws -> {
                    System.out.println(
                            "redis server is up! localhost:" + ws.getPort());
                })
                .exceptionally(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                    return null;
                });
        System.out.println("end!!!");
    }
}
