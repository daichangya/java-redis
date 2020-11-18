package com.daicy.redis.handler;

import com.daicy.redis.DefaultRequest;
import com.daicy.redis.RedisClientSession;
import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.Request;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import com.daicy.redis.protocal.RedisMessage;


/**
 * Handle decoded commands
 */
@ChannelHandler.Sharable
public class RedisCommandHandler extends SimpleChannelInboundHandler {

    private DefaultRedisServerContext redisServerContext;

    public RedisCommandHandler(DefaultRedisServerContext redisServerContext) {
        this.redisServerContext = redisServerContext;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        redisServerContext.addClient(
                new RedisClientSession(redisServerContext.sourceKey(ctx.channel()), ctx.channel()));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = new DefaultRequest((ArrayRedisMessage) msg,
                (RedisClientSession) redisServerContext.getClient(ctx.channel()), redisServerContext);
        RedisMessage reply = redisServerContext.executeCommand(request);
        ctx.write(reply);
//        if (redisCommand instanceof QuitCommand) {
//            ctx.close();
//        }
    }
}
