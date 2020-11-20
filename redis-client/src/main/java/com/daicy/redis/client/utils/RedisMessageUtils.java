package com.daicy.redis.client.utils;

import com.daicy.redis.client.codec.StringCodec;
import com.daicy.redis.protocal.BulkRedisMessage;
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

    public static MultiBulkRedisMessage toMultiBulkRedisMessage(ArrayRedisMessage arrayRedisMessage) {
        if (null == arrayRedisMessage || CollectionUtils.isEmpty(arrayRedisMessage.children())) {
            return new MultiBulkRedisMessage(null);
        }

        List<com.daicy.redis.protocal.RedisMessage> messageList =
                arrayRedisMessage.children().stream().map(param -> RedisMessageUtils.toString(param))
                        .map(paramStr -> new BulkRedisMessage(paramStr))
                        .collect(Collectors.toList());
        return new MultiBulkRedisMessage(messageList);
    }


    public static MultiBulkRedisMessage toRedisMessage(Collection<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return new MultiBulkRedisMessage(null);
        }
        List<com.daicy.redis.protocal.RedisMessage> bulkReplayList =
                values.stream().map(value -> new BulkRedisMessage(value)).collect(Collectors.toList());
        return new MultiBulkRedisMessage(bulkReplayList);
    }
}
