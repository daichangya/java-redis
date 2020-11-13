/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.utils;

import com.daicy.redis.storage.Dict;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.Lists;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.netty.handler.codec.redis.FullBulkStringRedisMessage.NULL_INSTANCE;
import static java.time.Instant.now;

public class DictUtils {

    public static RedisMessage convertValue(DictValue value) {
        if (value != null) {
            switch (value.getType()) {
                case STRING:
                    String string = value.getString();
                    return new SimpleStringRedisMessage(string);
//      case HASH:
//          ImmutableMap<String, String> map = value.getHash();
//          return array(keyValueList(map).toList());
//      case LIST:
//          ImmutableList<String> list = value.getList();
//          return convertArray(list.toList());
//      case SET:
//          ImmutableSet<String> set = value.getSet();
//          return convertArray(set.toSet());
//      case ZSET:
//          NavigableSet<Entry<Double, String>> zset = value.getSortedSet();
//          return convertArray(serialize(zset));
                default:
                    break;
            }
        }
        return FullBulkStringRedisMessage.NULL_INSTANCE;
    }

    public static DictValue getValue(RedisDb db, String key) {
        if (StringUtils.isEmpty(key) || null == db.getDict()) {
            return null;
        }
        DictKey dictKey = DictKey.safeKey(key);
        if (DictUtils.isExpired(db, dictKey)) {
            return null;
        }
        return db.getDict().get(dictKey);
    }

    public static List<DictValue> getValues(RedisDb db, List<String> keys) {
        if (CollectionUtils.isEmpty(keys) || null == db.getDict()) {
            return Lists.newArrayList();
        }
        return keys.stream().map(key -> new DictKey(key))
                .filter(dictKey -> !DictUtils.isExpired(db, dictKey))
                .map(dictKey -> db.getDict().get(dictKey))
                .collect(Collectors.toList());
    }

    public static Instant toInstantSs(long ttlSeconds) {
        return now().plusMillis(toMillis(ttlSeconds));
    }


    public static Instant toInstantMs(long ttlMillis) {
        return now().plusMillis(ttlMillis);
    }

    public static long toMillis(long ttlSeconds) {
        return TimeUnit.SECONDS.toMillis(ttlSeconds);
    }

    public static boolean isExpired(RedisDb db, DictKey dictKey) {
        Dict dbExpires = db.getExpires();
        DictValue expireValue = dbExpires.get(dictKey);
        if (null == expireValue) {
            return false;
        }
        boolean isExpired = expireValue.isExpired(Instant.now());
        if (isExpired) {
            dbExpires.remove(dictKey);
            db.getDict().remove(dictKey);
        }
        return isExpired;
    }
}
