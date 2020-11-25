package com.daicy.remoting.transport.netty4.redisclient;

import com.daicy.remoting.transport.netty4.client.ClientCallback;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handle decoded commands
 */
@Slf4j
@ChannelHandler.Sharable
public class RedisClientHandler extends ChannelDuplexHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClientHandler.class);

    private final AttributeKey<RedisCommand>  CURRENT_REQUEST = AttributeKey.valueOf("promise");

    private ClientCallback clientCallback;

    public RedisClientHandler(ClientCallback clientCallback) {
        this.clientCallback = clientCallback;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RedisCommand redisCommand =  ctx.channel().attr(CURRENT_REQUEST).get();
        redisCommand.getClientPromise().setSuccess(msg);
    }


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        RedisCommand redisCommand = (RedisCommand) msg;
        ctx.channel().attr(CURRENT_REQUEST).set(redisCommand);
        ctx.writeAndFlush(redisCommand.getRedisMessage());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clientCallback.onDisconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.debug("uncaught exception", cause);
        clientCallback.onDisconnect();
        ctx.close();
    }

}
