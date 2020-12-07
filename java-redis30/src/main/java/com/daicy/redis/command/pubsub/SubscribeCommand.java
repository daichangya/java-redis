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
import com.daicy.redis.protocal.RedisMessage;
import com.google.common.collect.Lists;

import java.util.List;

@ReadOnly
@Command("subscribe")
@ParamLength(1)
@PubSubAllowed
public class SubscribeCommand extends SubscriptionSupport {

    private static final String SUBSCRIBE = "subscribe";

    /* Subscribe a client to a channel. Returns 1 if the operation succeeded, or
     * 0 if the client was already subscribed to that channel.
     *
     * 设置客户端 c 订阅频道 channel 。
     *
     * 订阅成功返回 1 ，如果客户端已经订阅了该频道，那么返回 0 。
     */
    @Override
    public RedisMessage pubsubSubscribeChannel(Request request, String channel) {
        DefaultRedisServerContext redisServerContext = request.getServerContext();
        /* Add the channel to the client -> channels hash table */
        // 将 channels 填接到 c->pubsub_channels 的集合中（值为 NULL 的字典视为集合）
        RedisClientSession clientSession = request.getClientSession();
        if (clientSession.getPubsubChannels().add(channel)) {
            // 关联示意图
            // {
            //  频道名        订阅频道的客户端
            //  'channel-a' : [c1, c2, c3],
            //  'channel-b' : [c5, c2, c1],
            //  'channel-c' : [c10, c2, c1]
            // }
            /* Add the client to the channel -> list of clients hash table */
            // 从 pubsub_channels 字典中取出保存着所有订阅了 channel 的客户端的链表
            // 如果 channel 不存在于字典，那么添加进去
            List<String> clientSessions = redisServerContext.getPubsubChannels()
                    .getOrDefault(channel, Lists.newArrayList());
            clientSessions.add(clientSession.getId());
            redisServerContext.getPubsubChannels().put(channel, clientSessions);
            // before:
            // 'channel' : [c1, c2]
            // after:
            // 'channel' : [c1, c2, c3]
            // 将客户端添加到链表的末尾
        }

        /* Notify the client */
        // 回复客户端。
        // 示例：
        // redis 127.0.0.1:6379> SUBSCRIBE xxx
        // Reading messages... (press Ctrl-C to quit)
        // 1) "subscribe"
        // 2) "xxx"
        // 3) (integer) 1
        return getRedisMessage(clientSession, channel);
    }

    @Override
    public String getTitle() {
        return SUBSCRIBE;
    }
}
