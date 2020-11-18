package com.daicy.redis.protocal;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/17/20
 */
public class UnknownRedisMessage extends AbstractRedisMessage<String> {

    public UnknownRedisMessage(String value) {
        super(value, RedisMessageType.UNKNOWN);
    }

    @Override
    public byte getMarker() {
        return 0;
    }
}
