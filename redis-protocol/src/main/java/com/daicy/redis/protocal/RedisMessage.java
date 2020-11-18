package com.daicy.redis.protocal;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/16/20
 */
public interface RedisMessage<T> {

    T data();

    byte[] encode();

    byte getMarker();

    RedisMessageType getType();

}
