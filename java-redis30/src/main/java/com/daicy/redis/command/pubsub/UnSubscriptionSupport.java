/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.pubsub;

import com.daicy.redis.RedisClientSession;
import com.daicy.redis.Request;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public abstract class UnSubscriptionSupport implements BaseSubscriptionSupport, DBCommand {


    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        List<RedisMessage> resultMessage = Lists.newArrayList();

        List<String> params = request.getParamsStrList();
        RedisClientSession clientSession = request.getClientSession();
        if (CollectionUtils.isEmpty(params)) {
            params = Lists.newArrayList(getChannels(clientSession));
            if (CollectionUtils.isEmpty(params)) {
               return getRedisMessage(clientSession,null);
            }
        }
        for (String channel : params) {
            resultMessage.add(pubsubUnsubscribeChannel(clientSession, channel));
        }
        return new MultiBulkRedisMessage(resultMessage);
    }

    abstract List<String> getChannels(RedisClientSession clientSession);

    /* Unsubscribe a client from a channel. Returns 1 if the operation succeeded, or
     * 0 if the client was not subscribed to the specified channel.
     *
     * 客户端 c 退订频道 channel 。
     *
     * 如果取消成功返回 1 ，如果因为客户端未订阅频道，而造成取消失败，返回 0 。
     */
    abstract RedisMessage pubsubUnsubscribeChannel(RedisClientSession clientSession, String channel);

}
