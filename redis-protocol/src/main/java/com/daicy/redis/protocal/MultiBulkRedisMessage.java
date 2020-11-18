package com.daicy.redis.protocal;

import java.util.List;

import static com.daicy.redis.protocal.RedisMessageConstants.CRLF;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/16/20
 */
public class MultiBulkRedisMessage extends AbstractRedisMessage<List<RedisMessage>> {

    public MultiBulkRedisMessage(List<RedisMessage> redisMessages) {
        super(redisMessages,RedisMessageType.ARRAY);
    }

    @Override
    public byte[] encode() {
        ByteBufferBuilder builder = new ByteBufferBuilder();
        List<RedisMessage> redisMessageList = data();
        if (redisMessageList != null) {
            builder.append(getMarker()).append(redisMessageList.size()).append(CRLF);
            for (RedisMessage redisMessage : redisMessageList) {
                builder.append(redisMessage.encode());
            }
        } else {
            builder.append(getMarker()).append(0).append(CRLF);
        }
        return builder.build();
    }

    @Override
    public byte getMarker() {
        return RedisMessageConstants.ARRAY_PREFIX;
    }
}
