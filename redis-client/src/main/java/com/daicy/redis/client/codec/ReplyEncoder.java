/*
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.daicy.redis.client.codec;

import com.daicy.redis.client.utils.ByteBufUtils;
import com.daicy.redis.protocal.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.internal.UnstableApi;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Encodes {@link io.netty.handler.codec.redis.RedisMessage} into bytes following
 * <a href="http://redis.io/topics/protocol">RESP (REdis Serialization Protocol)</a>.
 */
@UnstableApi
public class ReplyEncoder extends MessageToMessageEncoder<RedisMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RedisMessage reply, List<Object> out) throws Exception {
        try {
            writeRedisMessage(reply, out);
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    private void writeRedisMessage(RedisMessage reply, List<Object> out) {
        if (reply instanceof StatusRedisMessage) {
            out.add(new SimpleStringRedisMessage(((StatusRedisMessage) reply).data()));
        } else if (reply instanceof ErrorRedisMessage) {
            out.add(new io.netty.handler.codec.redis.ErrorRedisMessage(((ErrorRedisMessage) reply).data()));
        } else if (reply instanceof IntegerRedisMessage) {
            out.add(new io.netty.handler.codec.redis.IntegerRedisMessage(((IntegerRedisMessage) reply).data()));
        } else if (reply instanceof BulkRedisMessage) {
            BulkRedisMessage bulkReply = (BulkRedisMessage) reply;
            if (null == bulkReply.data()) {
                out.add(FullBulkStringRedisMessage.NULL_INSTANCE);
            } else {
                out.add(new FullBulkStringRedisMessage(ByteBufUtils.toByteBuf(((BulkRedisMessage) reply).data())));
            }
        } else if (reply instanceof BulkByteRedisMessage) {
            BulkByteRedisMessage bulkReply = (BulkByteRedisMessage) reply;
            if (null == bulkReply.data()) {
                out.add(FullBulkStringRedisMessage.NULL_INSTANCE);
            } else {
                out.add(new FullBulkStringRedisMessage(ByteBufUtils.toByteBuf(((BulkRedisMessage) reply).encode())));
            }
        } else if (reply instanceof MultiBulkRedisMessage) {
            MultiBulkRedisMessage multiBulkReply = (MultiBulkRedisMessage) reply;
            if (null == multiBulkReply.data()) {
                out.add(ArrayRedisMessage.NULL_INSTANCE);
            } else {
                ArrayRedisMessage arrayRedisMessage = new ArrayRedisMessage(multiBulkReply.data().stream().map(
                        entry -> new FullBulkStringRedisMessage(ByteBufUtils.toByteBuf(((BulkRedisMessage) entry).data()))).collect(Collectors.toList()));
                out.add(arrayRedisMessage);
            }
        } else {
            throw new CodecException("unknown message type: " + reply);
        }
    }
}
