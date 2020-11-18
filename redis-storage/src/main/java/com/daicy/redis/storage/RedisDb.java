package com.daicy.redis.storage;


import com.daicy.redis.protocal.RedisMessage;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;

import static com.daicy.redis.protocal.RedisMessageConstants.TYPE_ERROR;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.storage
 * @date:11/13/20
 */
public class RedisDb {
    //    // 数据库键空间，保存着数据库中的所有键值对
//    dict *dict;                 /* The keyspace for this DB */
//
//    // 键的过期时间，字典的键为键，字典的值为过期事件 UNIX 时间戳
//    dict *expires;              /* Timeout of keys with a timeout set */
//
//    // 正处于阻塞状态的键
//    dict *blocking_keys;        /* Keys with clients waiting for data (BLPOP) */
//
//    // 可以解除阻塞的键
//    dict *ready_keys;           /* Blocked keys that received a PUSH */
//
//    // 正在被 WATCH 命令监视的键
//    dict *watched_keys;         /* WATCHED keys for MULTI/EXEC CAS */
//
//    struct evictionPoolEntry *eviction_pool;    /* Eviction pool of keys */
//
//    // 数据库号码
//    int id;                     /* Dict ID */
//
//    // 数据库的键的平均 TTL ，统计信息
//    long long avg_ttl;          /* Average TTL, just for stats */
    private Dict dict;

    private Dict expires;

    private Dict blocking_keys;

    private Dict ready_keys;

    private Dict watched_keys;

    private int id;

    private long avg_ttl;

    public Dict getDict() {
        return dict;
    }

    public void setDict(Dict dict) {
        this.dict = dict;
    }

    public Dict getExpires() {
        return expires;
    }

    public void setExpires(Dict expires) {
        this.expires = expires;
    }

    public Dict getBlocking_keys() {
        return blocking_keys;
    }

    public void setBlocking_keys(Dict blocking_keys) {
        this.blocking_keys = blocking_keys;
    }

    public Dict getReady_keys() {
        return ready_keys;
    }

    public void setReady_keys(Dict ready_keys) {
        this.ready_keys = ready_keys;
    }

    public Dict getWatched_keys() {
        return watched_keys;
    }

    public void setWatched_keys(Dict watched_keys) {
        this.watched_keys = watched_keys;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAvg_ttl() {
        return avg_ttl;
    }

    public void setAvg_ttl(long avg_ttl) {
        this.avg_ttl = avg_ttl;
    }

    public Pair<DictValue, RedisMessage> lookupKeyOrReply(String key, DataType dataType, RedisMessage reply) {
        DictKey dictKey = DictKey.safeKey(key);
        DictValue dictValue = lookupKeyOrExpire(dictKey);
        if (null == dictValue) {
            return Pair.of(dictValue, reply);
        }
        if (!dataType.equals(dictValue.getType())) {
            return Pair.of(dictValue, TYPE_ERROR);
        }
        return Pair.of(dictValue, null);
    }

    public DictValue lookupKeyOrExpire(DictKey dictKey) {
        boolean isExpired = expireIfNeeded(dictKey);
        if (isExpired) {
            return null;
        }
        return lookupKey(dictKey);
    }

    public DictValue lookupKey(DictKey dictKey) {
        return this.getDict().get(dictKey);
    }

    public boolean expireIfNeeded(DictKey dictKey) {
        Dict dbExpires = this.getExpires();
        DictValue expireValue = dbExpires.get(dictKey);
        if (null == expireValue) {
            return false;
        }
        boolean isExpired = expireValue.isExpired(Instant.now());
        if (isExpired) {
            dbExpires.remove(dictKey);
            this.getDict().remove(dictKey);
        }
        return isExpired;

    }
}
