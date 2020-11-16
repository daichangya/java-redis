package com.daicy.redis.utils;

import com.daicy.redis.codec.StringCodec;
import io.netty.handler.codec.redis.AbstractStringRedisMessage;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import com.daicy.redis.protocal.Reply;
import io.netty.handler.codec.redis.RedisMessage;

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
}
