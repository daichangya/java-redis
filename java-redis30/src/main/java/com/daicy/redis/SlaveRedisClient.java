package com.daicy.redis;

import com.daicy.remoting.transport.netty4.client.ClientCallback;
import com.daicy.redis.client.RedisClient2;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.remoting.transport.netty4.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionStage;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/20/20
 */
public class SlaveRedisClient implements ClientCallback, Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlaveRedisClient.class);

    private final BlockingQueue<RedisMessage> responses = new ArrayBlockingQueue<>(1);

    private RedisClient2 redisClient;

    public SlaveRedisClient(String host, int port) {
//        this.redisClient = new RedisClient2(host,port, this);
    }

    @Override
    public void onConnect() {
        System.out.println("connected!");
    }

    @Override
    public void onDisconnect() {
        System.out.println("disconnected!");
    }

    @Override
    public void onMessage(RedisMessage redisMessage) {
        try {
            responses.put(redisMessage);
        } catch (InterruptedException e) {
            LOGGER.warn("message not processed", e);
        }
    }

    public RedisMessage response() throws InterruptedException {
        return responses.take();
    }

    @Override
    public CompletionStage<Server> start() throws IOException {
        redisClient.start();
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public CompletionStage<Server> shutdown() {
//        redisClient.stop();
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public void init() {

    }
}