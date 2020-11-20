package com.daicy.redis.utils;

import com.daicy.redis.DefaultRequest;
import com.daicy.redis.Request;
import com.daicy.redis.protocal.RedisMessage;
import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.utils
 * @date:11/17/20
 */
public class RedisMessageUtilsTest {

    @Test
    public void toMultiBulkRedisMessage() {
        Request request = new DefaultRequest("set",
                Lists.newArrayList("daicy", "999"), null, null);
        RedisMessage redisMessage = DefaultRequest.toMultiBulkRedisMessage(request);
        System.out.println(new String(redisMessage.encode()));
    }
}