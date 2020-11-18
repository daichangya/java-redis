package com.daicy.redis.protocal;


/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/16/20
 */
public class IntegerRedisMessage extends AbstractRedisMessage<Integer> {

    public IntegerRedisMessage(Integer integer) {
        super(integer,RedisMessageType.INTEGER);
    }

    @Override
    public byte getMarker() {
        return  RedisMessageConstants.INTEGER_PREFIX;
    }

}
