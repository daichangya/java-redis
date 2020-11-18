package com.daicy.redis.protocal;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/16/20
 */
public class ErrorRedisMessage extends AbstractRedisMessage<String> {

    public ErrorRedisMessage(String message) {
        super(message, RedisMessageType.ERROR);
    }

    @Override
    public byte getMarker() {
        return RedisMessageConstants.ERROR_PREFIX;
    }

}
