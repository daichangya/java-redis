package com.daicy.redis.original;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.storage
 * @date:11/13/20
 */
public class EvictionPoolEntry {
//    unsigned long long idle;    /* Object idle time. */
//    sds key;                    /* Key name. */

    private long idle;

    private Object key;
}
