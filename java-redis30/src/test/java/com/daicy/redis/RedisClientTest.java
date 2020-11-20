package com.daicy.redis;

import com.daicy.redis.protocal.RedisMessage;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/20/20
 */
public class RedisClientTest implements ClientCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClientTest.class);

    private final BlockingQueue<RedisMessage> responses = new ArrayBlockingQueue<>(1);

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


    public static void main(String[] args) throws Exception {
        RedisClientTest redisClientTest = new RedisClientTest();
        RedisClient client = new RedisClient("localhost", 6379, redisClientTest);
        client.start();

        try {
            // Read commands from the stdin.
            System.out.println("Enter Redis commands (quit to end)");
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                final String input = in.readLine();
                final String line = input != null ? input.trim() : null;
                if (line == null || "quit".equalsIgnoreCase(line)) { // EOF or "quit"
                    break;
                } else if (line.isEmpty()) { // skip `enter` or `enter` with spaces.
                    continue;
                }
                String[] commands = line.split("\\s+");
                // Sends the received line to the server.
                lastWriteFuture = client.send(commands);
                System.out.println(new String(redisClientTest.response().encode()));
            }

        } finally {
            client.stop();
        }

    }


}