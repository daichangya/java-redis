/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.CharsetUtil;


public class DefaultRequest implements Request {

    private final String command;

    private final ArrayRedisMessage params;


    public DefaultRequest(String command, ArrayRedisMessage params) {
        this.command = Preconditions.checkNotNull(command);
        this.params = Preconditions.checkNotNull(params);
    }

    @Override
    public String getCommand() {
        return command.toString();
    }

    @Override
    public ArrayRedisMessage getParams() {
        return params;
    }

    @Override
    public RedisMessage getParam(int i) {
        if (i < params.children().size()) {
            RedisMessage redisMessage = params.children().get(i);
            if (redisMessage instanceof BulkStringRedisContent) {
                return ((BulkStringRedisContent) redisMessage).copy();
            }
            return redisMessage;
        }
        return null;
    }

    @Override
    public String getParamStr(int i) {
        RedisMessage redisMessage = getParam(i);
        if (null == redisMessage) {
            return null;
        }
        if (redisMessage instanceof BulkStringRedisContent) {
            return ((BulkStringRedisContent) redisMessage).content().toString(CharsetUtil.UTF_8);
        } else {
            return redisMessage.toString();
        }
    }


    @Override
    public int getLength() {
        return params.children().size();
    }

    @Override
    public boolean isEmpty() {
        return params.children().isEmpty();
    }

    @Override
    public boolean isExit() {
        return command.toString().equalsIgnoreCase("quit");
    }


    @Override
    public String toString() {
        return command + "[" + params.children().size() + "]: " + params;
    }
}
