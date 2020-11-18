package com.daicy.redis.context;


import com.daicy.redis.storage.RedisDb;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/11/20
 */
public interface RedisServerContext {

    public RedisDb getRedisDb(int id);

}
