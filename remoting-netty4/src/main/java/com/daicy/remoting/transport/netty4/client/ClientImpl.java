/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.remoting.transport.netty4.client;

import com.daicy.remoting.transport.netty4.Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.concurrent.GuardedBy;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.google.common.base.Preconditions.checkState;

@Slf4j
public class ClientImpl implements Client {

    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;

    private Channel channel;

    private final Object lock = new Object();

    @GuardedBy("lock")
    private boolean started;
    @GuardedBy("lock")
    private boolean shutdown;

    private final CompletableFuture<Server> startFuture = new CompletableFuture<>();
    private final CompletableFuture<Server> shutdownFuture = new CompletableFuture<>();
    private final CompletableFuture<Server> channelsUpFuture = new CompletableFuture<>();
    private final CompletableFuture<Server> channelsCloseFuture = new CompletableFuture<>();

    private ClientBuilder builder;

    public ClientImpl(ClientBuilder builder) {
        this.builder = builder;
    }

    @Override
    public CompletableFuture<Server> start() throws IOException {
        synchronized (lock) {
            checkState(!started, "Already started");
            checkState(!shutdown, "Shutting down");
            channelsUpFuture.thenAccept(this::started);
            channelsCloseFuture.whenComplete((webServer, throwable) -> shutdown(throwable));
            // Configure the server.
            workerGroup = new NioEventLoopGroup(builder.getIoWorkers());
            try {
                bootstrap = new Bootstrap().group(workerGroup)
                        .channel(NioSocketChannel.class)
//                      .handler(new LoggingHandler(LogLevel.INFO))
                        .option(ChannelOption.SO_BACKLOG, builder.getBacklog())
                        .option(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.SO_SNDBUF, builder.getSendBuffer())
                        .option(ChannelOption.SO_RCVBUF, builder.getRecvBuffer())
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .handler(builder.getChannelInitializer());

                bootstrap.connect(builder.getHost(), builder.getPort()).addListener(channelFuture -> {
                    String name = bootstrap.toString();
                    if (!channelFuture.isSuccess()) {
                        log.info("Channel '" + name + "' startup failed with message '"
                                + channelFuture.cause().getMessage() + "'.");
                        channelsUpFuture.completeExceptionally(new IllegalStateException("Channel startup failed: " + name,
                                channelFuture.cause()));
                        return;
                    }

                    channel = ((ChannelFuture) channelFuture).channel();
                    log.info("Channel '" + name + "' started: " + channel);

                    channel.closeFuture().addListener(future -> {
                        log.info("Channel '" + name + "' closed: " + channel);
                        if (channelsUpFuture.isCompletedExceptionally()) {
                            if (future.cause() != null) {
                                log.warn(
                                        "Startup failure channel close failure",
                                        new IllegalStateException(future.cause()));
                            }
                        } else {
                            if (!future.isSuccess()) {
                                channelsCloseFuture.completeExceptionally(new IllegalStateException("Channel stop failure.",
                                        future.cause()));
                            } else if (null == channel) {
                                channelsCloseFuture.complete(this);
                            }
                            // else we're waiting for the rest of the channels to start, successful branch
                        }
                    });

                    if (channelsUpFuture.isCompletedExceptionally()) {
                        channel.close();
                    }

                    if (null != channel) {
                        log.info("All channels started: ");
                        started = true;
                        channelsUpFuture.complete(this);
                    }
                });

            } catch (Exception ex) {
                log.error("Start application failed, cause: " + ex.getMessage(), ex);
                startFuture.completeExceptionally(ex);
            }
            return startFuture;
        }
    }

    private void started(Server server) {
        builder.getServerContext().setServer(server);
        startFuture.complete(server);
    }


    @Override
    public int getPort() {
        synchronized (lock) {
            checkState(started, "Not started");
            return builder.getPort();
        }
    }

    private void shutdown(Throwable cause) {
        workerGroup.shutdownGracefully();
        shutdownFuture.complete(this);
        started = false;
        log.info("shutdowned!");
    }

    @Override
    public CompletableFuture<Server> shutdown() {
        synchronized (lock) {
            if (!shutdown) {
                shutdown = true;
                if (!startFuture.isDone()) {
                    startFuture.cancel(true);
                }
                channel.close();
                log.info("shutdowning!");
            }
            builder.getServerContext().stop();
            channelsCloseFuture.complete(this);
            return shutdownFuture;
        }
    }


    @Override
    public boolean isShutdown() {
        synchronized (lock) {
            return shutdown;
        }
    }

    @Override
    public SocketAddress getLocalAddress() {
        synchronized (lock) {
            checkState(started, "Not started");
            return channel.localAddress();
        }
    }

    @Override
    public void init() {
        builder.getServerContext().init();
        builder.getServerContext().start();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void reconnect() {
        synchronized (lock) {
            try {
                if (!shutdown) {
                    start();
                }
            } catch (IOException e) {
                log.error("client reconnect error", e);
            }
        }
    }

    @Override
    public ClientBuilder getClientBuilder() {
        return builder;
    }

    @Override
    public EventLoopGroup getEventLoopGroup() {
        return bootstrap.config().group();
    }
}
