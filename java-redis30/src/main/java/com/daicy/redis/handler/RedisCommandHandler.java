package com.daicy.redis.handler;

import com.daicy.redis.DefaultRequest;
import com.daicy.redis.RedisClientSession;
import com.daicy.redis.RedisServerContext;
import com.daicy.redis.Request;
import com.daicy.redis.command.QuitCommand;
import com.daicy.redis.command.RedisCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;


/**
 * Handle decoded commands
 */
@ChannelHandler.Sharable
public class RedisCommandHandler extends SimpleChannelInboundHandler {

    private  RedisServerContext redisServerContext;

    public RedisCommandHandler(RedisServerContext redisServerContext) {
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
                (RedisClientSession) redisServerContext.getClient(ctx.channel()));
        RedisCommand redisCommand = redisServerContext.getRedisCommand(request.getCommand());
        RedisMessage reply = redisCommand.execute(request);
        ctx.write(reply);
        if (redisCommand instanceof QuitCommand) {
            ctx.close();
        }
    }
}
