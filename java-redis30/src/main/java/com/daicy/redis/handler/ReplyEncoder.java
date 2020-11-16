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

package com.daicy.redis.handler;

import com.daicy.redis.protocal.*;
import com.daicy.redis.utils.ByteBufUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.redis.*;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.UnstableApi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Encodes {@link RedisMessage} into bytes following
 * <a href="http://redis.io/topics/protocol">RESP (REdis Serialization Protocol)</a>.
 */
@UnstableApi
public class ReplyEncoder extends MessageToMessageEncoder<Reply> {

    private final RedisMessagePool messagePool;

    /**
     * Creates a new instance with default {@code messagePool}.
     */
    public ReplyEncoder() {
        this(FixedRedisMessagePool.INSTANCE);
    }

    /**
     * Creates a new instance.
     *
     * @param messagePool the predefined message pool.
     */
    public ReplyEncoder(RedisMessagePool messagePool) {
        this.messagePool = ObjectUtil.checkNotNull(messagePool, "messagePool");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Reply reply, List<Object> out) throws Exception {
        try {
            writeRedisMessage(reply, out);
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    private void writeRedisMessage(Reply reply, List<Object> out) {
        if (reply instanceof StatusReply) {
            out.add(new SimpleStringRedisMessage(((StatusReply) reply).data()));
        } else if (reply instanceof ErrorReply) {
            out.add(new ErrorRedisMessage(((ErrorReply) reply).data()));
        } else if (reply instanceof IntegerReply) {
            out.add(new IntegerRedisMessage(((IntegerReply) reply).data()));
        } else if (reply instanceof BulkReply) {
            BulkReply bulkReply = (BulkReply) reply;
            if (null == bulkReply.data()) {
                out.add(FullBulkStringRedisMessage.NULL_INSTANCE);
            } else {
                out.add(new FullBulkStringRedisMessage(ByteBufUtils.toByteBuf(((BulkReply) reply).data())));
            }
        } else if (reply instanceof MultiBulkReply) {
            MultiBulkReply multiBulkReply = (MultiBulkReply) reply;
            if (null == multiBulkReply.data()) {
                out.add(ArrayRedisMessage.NULL_INSTANCE);
            } else {
                ArrayRedisMessage arrayRedisMessage = new ArrayRedisMessage(multiBulkReply.data().stream().map(
                        entry -> new FullBulkStringRedisMessage(ByteBufUtils.toByteBuf(((BulkReply) entry).data()))).collect(Collectors.toList()));
                out.add(arrayRedisMessage);
            }
        } else {
            throw new CodecException("unknown message type: " + reply);
        }
    }
}
