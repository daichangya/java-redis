package com.daicy.redis.utils;

import com.daicy.redis.DefaultRequest;
import com.daicy.redis.RedisClientSession;
import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.Request;
import com.daicy.redis.codec.StringCodec;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.google.common.collect.Lists;
import io.netty.handler.codec.redis.AbstractStringRedisMessage;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.handler.codec.redis.RedisMessage;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

    public static MultiBulkRedisMessage toMultiBulkRedisMessage(ArrayRedisMessage arrayRedisMessage) {
        if (null == arrayRedisMessage || CollectionUtils.isEmpty(arrayRedisMessage.children())) {
            return new MultiBulkRedisMessage(null);
        }

        List<com.daicy.redis.protocal.RedisMessage> messageList =
                arrayRedisMessage.children().stream().map(param -> RedisMessageUtils.toString(param))
                        .map(paramStr -> new BulkRedisMessage(paramStr))
                        .collect(Collectors.toList());
        return new MultiBulkRedisMessage(messageList);
    }

    public static Request toRequest(MultiBulkRedisMessage multiBulkRedisMessage, DefaultRedisServerContext redisServerContext) {
        if (null == multiBulkRedisMessage || CollectionUtils.isEmpty(multiBulkRedisMessage.data())) {
            return null;
        }
        List<String> params = multiBulkRedisMessage.data().stream()
                .map(redisMessage -> ((BulkRedisMessage)redisMessage).data()).collect(Collectors.toList());
        RedisClientSession clientSession = new RedisClientSession("dummy", null);
        return new DefaultRequest(params.get(0),params.subList(1,params.size()),clientSession,redisServerContext);
    }


    public static MultiBulkRedisMessage toMultiBulkRedisMessage(Request request) {
        BulkRedisMessage bulkReply = new BulkRedisMessage(request.getCommand());
        List<com.daicy.redis.protocal.RedisMessage> bulkReplyList = Lists.newArrayList(bulkReply);
        if (CollectionUtils.isNotEmpty(request.getParamsStrList())) {
            bulkReplyList.addAll(request.getParamsStrList().stream()
                    .map(param -> new BulkRedisMessage(param)).collect(toList()));
        }
        return new MultiBulkRedisMessage(bulkReplyList);
    }


    public static MultiBulkRedisMessage toRedisMessage(Collection<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return new MultiBulkRedisMessage(null);
        }
        List<com.daicy.redis.protocal.RedisMessage> bulkReplayList =
                values.stream().map(value -> new BulkRedisMessage(value)).collect(Collectors.toList());
        return new MultiBulkRedisMessage(bulkReplayList);
    }
}
