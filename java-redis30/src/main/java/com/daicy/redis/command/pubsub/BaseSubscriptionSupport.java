/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.pubsub;

import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.RedisClientSession;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.google.common.collect.Lists;

import java.util.List;

import static com.daicy.redis.protocal.RedisMessageConstants.NULL;

public interface BaseSubscriptionSupport {

    default int publish(DefaultRedisServerContext redisServerContext, List<String> sessionIds, RedisMessage redisMessage) {
        sessionIds.forEach(sessionId -> {
            RedisClientSession redisClientSession = redisServerContext.getClient(sessionId);
            if (null != redisClientSession) {
                redisClientSession.getChannel().writeAndFlush(redisMessage);
            }
        });
        return sessionIds.size();
    }

    public String getTitle();

    default RedisMessage getRedisMessage(RedisClientSession clientSession, String channel) {
        List<RedisMessage> resultMessage = Lists.newArrayList();
        resultMessage.add(new BulkRedisMessage(getTitle()));
        resultMessage.add(null == channel ? NULL : new BulkRedisMessage(channel));
        resultMessage.add(new IntegerRedisMessage(clientSession.getPubsubPatterns().size() + clientSession.getPubsubChannels().size()));
        return new MultiBulkRedisMessage(resultMessage);
    }


}
