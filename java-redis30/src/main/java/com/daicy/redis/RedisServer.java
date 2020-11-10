package com.daicy.redis;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * Redis server
 */
public class RedisServer {
    private static Integer port = 6380;

    public static void main(String[] args) throws InterruptedException {

        // Only execute the command handler in a single thread
        final RedisCommandHandler commandHandler = new RedisCommandHandler();

        // Configure the server.
        ServerBootstrap b = new ServerBootstrap();
        final DefaultEventExecutorGroup group = new DefaultEventExecutorGroup(1);
        try {
            b.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .localAddress(port)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new RedisDecoder());
                            p.addLast(new RedisBulkStringAggregator());
                            p.addLast(new RedisArrayAggregator());
                            p.addLast(new RedisEncoder());
                            p.addLast(group, commandHandler);
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind().sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
