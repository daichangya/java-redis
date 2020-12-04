package com.daicy.redis.client.utils;

import com.daicy.redis.client.codec.StringCodec;
import com.daicy.redis.protocal.BulkByteRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import io.netty.handler.codec.redis.AbstractStringRedisMessage;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.handler.codec.redis.RedisMessage;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.utils
 * @date:11/13/20
 */
public class RedisMessageUtils {

    private static final StringCodec stringCodec = new StringCodec();

    public static String toString(RedisMessage redisMessage) {
        if (null == redisMessage) {
            return null;
        }

        if (redisMessage instanceof AbstractStringRedisMessage) {
            return ((AbstractStringRedisMessage) redisMessage).content();
        } else if (redisMessage instanceof BulkStringRedisContent) {
            return stringCodec.decodeValue(((BulkStringRedisContent) redisMessage).content());
        } else {
            return redisMessage.toString();
        }
    }

    public static byte[] toByte(RedisMessage redisMessage) {
        if (null == redisMessage) {
            return null;
        }

        if (redisMessage instanceof AbstractStringRedisMessage) {
            return ((AbstractStringRedisMessage) redisMessage).content().getBytes();
        } else if (redisMessage instanceof BulkStringRedisContent) {
            return ByteBufUtils.getBytes(((BulkStringRedisContent) redisMessage).content());
        } else {
            return redisMessage.toString().getBytes();
        }
    }

    public static MultiBulkRedisMessage toMultiBulkRedisMessage(ArrayRedisMessage arrayRedisMessage) {
        if (null == arrayRedisMessage || CollectionUtils.isEmpty(arrayRedisMessage.children())) {
            return new MultiBulkRedisMessage(null);
        }

        List<com.daicy.redis.protocal.RedisMessage> messageList =
                arrayRedisMessage.children().stream().map(param -> RedisMessageUtils.toByte(param))
                        .map(paramStr -> new BulkByteRedisMessage(paramStr))
                        .collect(Collectors.toList());
        return new MultiBulkRedisMessage(messageList);
    }


    public static MultiBulkRedisMessage toRedisMessage(Collection<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return new MultiBulkRedisMessage(null);
        }
        List<com.daicy.redis.protocal.RedisMessage> bulkReplayList =
                values.stream().map(value -> new BulkByteRedisMessage(value.getBytes())).collect(Collectors.toList());
        return new MultiBulkRedisMessage(bulkReplayList);
    }
}
