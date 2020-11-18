package com.daicy.redis.protocal;

import com.google.common.base.Preconditions;

import static com.daicy.redis.protocal.RedisMessageConstants.CRLF;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/17/20
 */
public abstract class AbstractRedisMessage<T> implements RedisMessage {

    private T value;

    private RedisMessageType type;

    public AbstractRedisMessage(T value, RedisMessageType type) {
        this.value = value;
        this.type = Preconditions.checkNotNull(type);
    }

    @Override
    public byte[] encode() {
        ByteBufferBuilder builder = new ByteBufferBuilder();
        builder.append(getMarker()).append(value).append(CRLF);
        return builder.build();
    }

    @Override
    public T data() {
        return value;
    }

    @Override
    public RedisMessageType getType() {
        return type;
    }
}
