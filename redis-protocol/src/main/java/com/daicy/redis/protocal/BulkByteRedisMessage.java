package com.daicy.redis.protocal;

import ch.qos.logback.core.CoreConstants;

import static com.daicy.redis.protocal.RedisMessageConstants.CRLF;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/16/20
 */
public class BulkByteRedisMessage extends AbstractRedisMessage<byte[]> {

    public BulkByteRedisMessage(byte[] bytes) {
        super(bytes, RedisMessageType.STRING);
    }

    @Override
    public byte[] encode() {
        ByteBufferBuilder builder = new ByteBufferBuilder();
        if (data() != null) {
            byte[] bytes = data();
            builder.append(getMarker()).append(bytes.length).append(CRLF).append(bytes);
        } else {
            builder.append(getMarker()).append(-1);
        }
        builder.append(CRLF);
        return builder.build();
    }

    @Override
    public byte getMarker() {
        return CoreConstants.DOLLAR;
    }

}
