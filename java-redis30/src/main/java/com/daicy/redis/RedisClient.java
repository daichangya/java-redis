/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis;

import com.daicy.redis.handler.RedisClientInitializer;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class RedisClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClient.class);

    private static final int BUFFER_SIZE = 1024 * 1024;

    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private ChannelFuture future;
    private ChannelHandlerContext context;
    private final ClientCallback callback;

    private final int port;
    private final String host;

    public RedisClient(String host, int port, ClientCallback callback) {
        this.callback = callback;
        this.port = port;
        this.host = host;
    }

    public void start() {
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        bootstrap = new Bootstrap().group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
                .option(ChannelOption.SO_SNDBUF, BUFFER_SIZE)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new RedisClientInitializer(this));


        future = connect();

    }

    public void stop() {
        try {
            if (future != null) {
                future.channel().close().syncUninterruptibly();
                future = null;
            }
        } finally {
            if (workerGroup != null) {
                workerGroup.shutdownGracefully().syncUninterruptibly();
                workerGroup = null;
            }
        }
    }


    public void connected(ChannelHandlerContext ctx) {
        LOGGER.info("channel active");
        this.context = ctx;
        callback.onConnect();
    }

    public void disconnected(ChannelHandlerContext ctx) {
        LOGGER.info("client disconected from server: {}:{}", host, port);
        if (this.context != null) {
            callback.onDisconnect();
            this.context = null;
            if (future != null) {
                future.channel().eventLoop().schedule(this::start, 1L, TimeUnit.SECONDS);
            }
        }
    }

    public ChannelFuture send(String... message) {
        return send(new MultiBulkRedisMessage(asList(message).stream().map(RedisMessage::string).collect(toList())));
    }

    public ChannelFuture send(RedisMessage message) {
        return writeAndFlush(message);
    }

    public void receive(ChannelHandlerContext ctx, RedisMessage redisMessage) {
        callback.onMessage(redisMessage);
    }

    private ChannelFuture connect() {
        LOGGER.info("trying to connect");
        return bootstrap.connect(host, port);
    }

    private ChannelFuture writeAndFlush(Object message) {
        if (context != null) {
            return context.writeAndFlush(message);
        }
        return null;
    }

}
