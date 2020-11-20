package com.daicy.redis.client.handler;

import com.daicy.redis.client.RedisClient;
import com.daicy.redis.protocal.RedisMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handle decoded commands
 */
@ChannelHandler.Sharable
public class RedisClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClientHandler.class);

    private final RedisClient redisClient;

    public RedisClientHandler(RedisClient redisClient) {
        this.redisClient = redisClient;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        redisClient.connected(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            redisClient.receive(ctx, (RedisMessage) msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        redisClient.disconnected(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.debug("uncaught exception", cause);
        redisClient.disconnected(ctx);
        ctx.close();
    }

}
