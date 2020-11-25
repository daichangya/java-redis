package com.daicy.remoting.transport.netty4.redisclient;

import com.daicy.remoting.transport.netty4.client.Client;
import com.daicy.remoting.transport.netty4.client.ClientBuilder;
import com.daicy.remoting.transport.netty4.client.ClientCallback;
import com.daicy.remoting.transport.netty4.client.ClientPromise;
import com.daicy.remoting.transport.netty4.client.handler.ClientInitializer;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.redis.*;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.remoting.transport.netty4
 * @date:11/23/20
 */
@Slf4j
public class RedisClient implements ClientCallback<RedisMessage> {


    private final Client client;

    public RedisClient() throws Exception {
        ClientBuilder clientBuilder = (ClientBuilder) ClientBuilder.forHostPort("localhost", 6379)
                .channelInitializer(new ClientInitializer(this));
        Client client = clientBuilder.build();
        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));
        client.init();
        client.start().get();
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void channel(SocketChannel channel) {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new RedisDecoder());
        p.addLast(new RedisBulkStringAggregator());
        p.addLast(new RedisArrayAggregator());
        p.addLast(new RedisEncoder());
        p.addLast(new RedisClientHandler(this));
    }

    @Override
    public void onConnect() {
        System.out.println("connected!");
    }

    @Override
    public void onDisconnect() {
        System.out.println("disconnected! 88");
//        client.reconnect();
    }


    public ClientPromise send(RedisMessage redisMessage, long timeout) {
        ClientPromise promise = new ClientPromise();
        if (timeout == -1) {
            timeout = client.getClientBuilder().getSessionTimeout();
        }

        if (client.getEventLoopGroup().isShuttingDown()) {
            RedisException cause = new RedisException("Redis is shutdown");
            return ClientPromise.newFailedFuture((Throwable) cause);
        }

        ScheduledFuture<?> scheduledFuture = client.getEventLoopGroup().schedule(new Runnable() {
            @Override
            public void run() {
                RedisException ex = new RedisException("Command execution timeout for command: "
                        + redisMessage.toString()
                        + ", Redis client: " + client.getClientBuilder().getHost());
                promise.tryFailure(ex);
            }
        }, timeout, TimeUnit.MILLISECONDS);

        promise.onComplete((res, e) -> {
            scheduledFuture.cancel(false);
        });

        ChannelFuture writeFuture = send(new RedisCommand(redisMessage, promise));
        writeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    promise.tryFailure(future.cause());
                }
            }
        });
        return promise;
    }

    public ChannelFuture send(RedisCommand redisCommand) {
        return getClient().getChannel().writeAndFlush(redisCommand);
    }


    public static void main(String[] args) throws Exception {
        RedisClient redisClient = new RedisClient();
        String[] commands = "keys *".split("\\s+");
        List<RedisMessage> children = new ArrayList<RedisMessage>(commands.length);
        for (String cmdString : commands) {
            children.add(new FullBulkStringRedisMessage(Unpooled.wrappedBuffer(cmdString.getBytes())));
        }
        RedisMessage redisMessage = new ArrayRedisMessage(children);
        ClientPromise promise = redisClient.send(redisMessage,-1);
        System.out.println(promise.get());
    }
}