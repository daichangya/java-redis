package com.daicy.redis;

import com.daicy.redis.client.RedisClient;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.remoting.transport.netty4.client.ClientBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.remoting.transport.netty4
 * @date:11/23/20
 */
@Slf4j
public class SlaveRedisClient extends RedisClient {


    public SlaveRedisClient(ClientBuilder clientBuilder) throws Exception {
        super(clientBuilder);
    }

    @Override
    public void onMessage(RedisMessage redisMessage) {
        DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
        if (redisMessage instanceof MultiBulkRedisMessage) {
            redisServerContext.processCommand(redisMessage);
        }
    }
}