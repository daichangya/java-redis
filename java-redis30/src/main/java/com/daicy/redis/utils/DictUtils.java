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
import org.apache.commons.collections4.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.Instant.now;

public class DictUtils {

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
