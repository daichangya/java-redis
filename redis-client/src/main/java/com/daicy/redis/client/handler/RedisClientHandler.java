package com.daicy.redis.client.handler;

import com.daicy.redis.client.MessageCallback;
import com.daicy.redis.client.RedisCommand;
import com.daicy.redis.client.codec.SyncRedisDecoder;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private MessageCallback clientCallback;

    public RedisClientHandler(MessageCallback clientCallback) {
        this.clientCallback = clientCallback;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        clientCallback.onConnect();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RedisCommand redisCommand = ctx.channel().attr(CURRENT_REQUEST).getAndRemove();
        if (null == redisCommand) {
            clientCallback.onMessage((RedisMessage) msg);
        } else {
            redisCommand.getClientPromise().setSuccess(msg);
        }
    }


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        RedisCommand redisCommand = (RedisCommand) msg;
        Object redisMessage = redisCommand.getRedisMessage();
        if(StringUtils.equals(redisCommand.getCommandName(),"sync")){
            ctx.pipeline().replace("redisDecoder","syncRedisDecoder",new SyncRedisDecoder());
        }
        ctx.channel().attr(CURRENT_REQUEST).set(redisCommand);
        ctx.writeAndFlush(redisMessage);
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
