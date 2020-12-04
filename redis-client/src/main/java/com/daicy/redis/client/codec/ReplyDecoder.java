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
import com.daicy.redis.client.utils.RedisMessageUtils;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.UnstableApi;

import java.util.List;

import static com.daicy.redis.protocal.RedisMessageConstants.NULL;

/**
 * Encodes {@link io.netty.handler.codec.redis.RedisMessage} into bytes following
 * <a href="http://redis.io/topics/protocol">RESP (REdis Serialization Protocol)</a>.
 */
@UnstableApi
public class ReplyDecoder extends MessageToMessageDecoder<io.netty.handler.codec.redis.RedisMessage> {


    @Override
    protected void decode(ChannelHandlerContext ctx, io.netty.handler.codec.redis.RedisMessage msg, List<Object> out) throws Exception {
        try {
            writeRedisMessage(msg, out);
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    private void writeRedisMessage(io.netty.handler.codec.redis.RedisMessage msg, List<Object> out) {
        if (msg instanceof SimpleStringRedisMessage) {
            out.add(RedisMessage.string(((SimpleStringRedisMessage) msg).content()));
        } else if (msg instanceof io.netty.handler.codec.redis.ErrorRedisMessage) {
            out.add(new ErrorRedisMessage(((io.netty.handler.codec.redis.ErrorRedisMessage) msg).content()));
        } else if (msg instanceof io.netty.handler.codec.redis.IntegerRedisMessage) {
            out.add(new IntegerRedisMessage((int) ((io.netty.handler.codec.redis.IntegerRedisMessage) msg).value()));
        } else if (msg instanceof FullBulkStringRedisMessage) {
            FullBulkStringRedisMessage fullBulkStringRedisMessage = (FullBulkStringRedisMessage) msg;
            if (fullBulkStringRedisMessage.isNull()) {
                out.add(NULL);
            } else {
                out.add(RedisMessage.bytes(ByteBufUtils.getBytes(fullBulkStringRedisMessage.content())));
            }
        } else if (msg instanceof ArrayRedisMessage) {
            out.add(RedisMessageUtils.toMultiBulkRedisMessage((ArrayRedisMessage) msg));
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }

}
