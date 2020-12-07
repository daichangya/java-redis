/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.pubsub;


import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.RedisClientSession;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.PubSubAllowed;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

@ReadOnly
@Command("punsubscribe")
@ParamLength(1)
@PubSubAllowed
public class PatternUnsubscribeCommand extends UnsubscribeCommand {

    private static final String PUNSUBSCRIBE = "punsubscribe";

    /* Unsubscribe a client from a channel. Returns 1 if the operation succeeded, or
     * 0 if the client was not subscribed to the specified channel.
     *
     * 客户端 c 退订频道 channel 。
     *
     * 如果取消成功返回 1 ，如果因为客户端未订阅频道，而造成取消失败，返回 0 。
     */
    @Override
    RedisMessage pubsubUnsubscribeChannel(RedisClientSession clientSession, String channel) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        if (clientSession.getPubsubPatterns().remove(channel)) {
            redisServerContext.getPubsubPatterns().get(channel).remove(clientSession.getId());
        }

        return getRedisMessage(clientSession, channel);
    }

    @Override
    public String getTitle() {
        return PUNSUBSCRIBE;
    }
}
