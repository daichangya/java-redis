package com.daicy.redis;

import com.daicy.redis.command.CommandSuite;
import com.daicy.redis.command.DBCommandSuite;
import com.daicy.redis.command.QuitCommand;
import com.daicy.redis.command.RedisCommand;
import com.daicy.redis.utils.ByteBufUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

import java.util.List;

import static io.netty.util.CharsetUtil.UTF_8;


/**
 * Handle decoded commands
 */
@ChannelHandler.Sharable
public class RedisCommandHandler extends SimpleChannelInboundHandler {

    private static final byte LOWER_DIFF = 'a' - 'A';

    private static final CommandSuite commandSuite = new DBCommandSuite();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        List<RedisMessage> messageList = ((ArrayRedisMessage) msg).children();
        FullBulkStringRedisMessage fullBulkStringRedisMessage = (FullBulkStringRedisMessage) messageList.get(0);
        byte[] name = ByteBufUtils.getBytes(fullBulkStringRedisMessage.content());
        Request request = new DefaultRequest(new String(name).toLowerCase(),
                new ArrayRedisMessage(messageList.subList(1, messageList.size())));
        RedisCommand redisCommand = commandSuite.getCommand(request.getCommand());
        RedisMessage reply = redisCommand.execute(request);
        ctx.write(reply);
        if (redisCommand instanceof QuitCommand) {
            ctx.close();
        }
    }
}
