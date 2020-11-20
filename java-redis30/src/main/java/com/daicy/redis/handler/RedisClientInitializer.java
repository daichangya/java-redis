/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.daicy.redis.handler;

import com.daicy.redis.RedisClient;
import com.daicy.redis.codec.ReplyDecoder;
import com.daicy.redis.codec.ReplyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;

public class RedisClientInitializer extends ChannelInitializer<SocketChannel> {

    private final RedisClient redisClient;

    public RedisClientInitializer(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new RedisDecoder());
        p.addLast(new RedisBulkStringAggregator());
        p.addLast(new RedisArrayAggregator());
        p.addLast(new ReplyDecoder());
        p.addLast(new RedisEncoder());
        p.addLast(new ReplyEncoder());
        p.addLast(new RedisClientHandler(redisClient));
    }

}
