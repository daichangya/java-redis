/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis;

import com.daicy.redis.utils.ByteBufUtils;
import com.daicy.redis.utils.RedisMessageUtils;
import com.google.common.base.Preconditions;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

import java.util.List;
import java.util.stream.Collectors;


public class DefaultRequest implements Request {

    private final String command;

    private final RedisClientSession clientSession;

    private final List<String> paramsStrList;

    private final DefaultRedisServerContext serverContext;

    public DefaultRequest(ArrayRedisMessage arrayRedisMessage, RedisClientSession clientSession
            , DefaultRedisServerContext serverContext) {
        List<RedisMessage> messageList = arrayRedisMessage.children();
        FullBulkStringRedisMessage fullBulkStringRedisMessage =
                (FullBulkStringRedisMessage) messageList.get(0);
        byte[] name = ByteBufUtils.getBytes(fullBulkStringRedisMessage.content());
        this.command = Preconditions.checkNotNull(new String(name).toLowerCase());
        List<RedisMessage> params = messageList.subList(1, messageList.size());
        this.paramsStrList = params.stream().map(param -> RedisMessageUtils.toString(param))
                .collect(Collectors.toList());
        this.clientSession = Preconditions.checkNotNull(clientSession);
        this.serverContext = serverContext;
    }

    public DefaultRequest(String command,List<String> paramsStrList, RedisClientSession clientSession
            , DefaultRedisServerContext serverContext) {
        this.command = Preconditions.checkNotNull(command.toLowerCase());
        this.paramsStrList = paramsStrList;
        this.clientSession = clientSession;
        this.serverContext = serverContext;
    }

    @Override
    public String getCommand() {
        return command.toString();
    }


    @Override
    public String getParamStr(int i) {
        return i < paramsStrList.size() ? paramsStrList.get(i) : null;
    }

    @Override
    public List<String> getParamsStrList() {
        return paramsStrList;
    }

    @Override
    public int getLength() {
        return paramsStrList.size();
    }

    @Override
    public RedisClientSession getClientSession() {
        return clientSession;
    }

    @Override
    public DefaultRedisServerContext getServerContext() {
        return serverContext;
    }

    @Override
    public boolean isEmpty() {
        return paramsStrList.isEmpty();
    }

    @Override
    public boolean isExit() {
        return command.toString().equalsIgnoreCase("quit");
    }


    @Override
    public String toString() {
        return command + "[" + paramsStrList.size() + "]: " + paramsStrList;
    }
}
