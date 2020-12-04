package com.daicy.redis.protocal;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/16/20
 */
public class BulkRedisMessage extends BulkByteRedisMessage{

    public BulkRedisMessage(String message) {
        super(message.getBytes());
    }
}
