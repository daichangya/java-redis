/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis;

import com.daicy.redis.utils.ByteBufUtils;
import com.daicy.redis.utils.RedisMessageUtils;
import com.google.common.base.Preconditions;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

import java.util.List;
import java.util.stream.Collectors;


public class DefaultRequest implements Request {

    private final String command;

    private final ArrayRedisMessage params;

    private final RedisClientSession clientSession;

    private final List<String> paramsStrList;

    public DefaultRequest(ArrayRedisMessage arrayRedisMessage, RedisClientSession clientSession) {
        List<RedisMessage> messageList = arrayRedisMessage.children();
        FullBulkStringRedisMessage fullBulkStringRedisMessage =
                (FullBulkStringRedisMessage) messageList.get(0);
        byte[] name = ByteBufUtils.getBytes(fullBulkStringRedisMessage.content());
        this.command = Preconditions.checkNotNull(new String(name).toLowerCase());
        this.params = Preconditions.checkNotNull(new ArrayRedisMessage(messageList.subList(1, messageList.size())));
        this.paramsStrList = params.children().stream().map(param -> RedisMessageUtils.toString(param))
                .collect(Collectors.toList());
        this.clientSession = Preconditions.checkNotNull(clientSession);
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
                return ((BulkStringRedisContent) redisMessage).duplicate();
            }
            return redisMessage;
        }
        return null;
    }

    @Override
    public String getParamStr(int i) {
        return i < paramsStrList.size() ? null : paramsStrList.get(i);
    }

    @Override
    public List<String> getParamsStrList() {
        return paramsStrList;
    }

    @Override
    public int getLength() {
        return params.children().size();
    }

    @Override
    public RedisClientSession getClientSession() {
        return clientSession;
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
