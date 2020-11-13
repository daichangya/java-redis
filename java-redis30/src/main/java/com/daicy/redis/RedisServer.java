package com.daicy.redis;

import com.daicy.redis.handler.RedisServerInitializer;
import com.daicy.remoting.transport.netty4.Server;
import com.daicy.remoting.transport.netty4.ServerBuilder;

/**
 * Redis server
 */
public class RedisServer {
    private static Integer port = 6380;

    public static void main(String[] args) throws Exception {
        Server redisServer = ServerBuilder.forPort(port).channelInitializer(new RedisServerInitializer()).build();
        RedisServerContext.getInstance().setServer(redisServer);
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

    }
}
