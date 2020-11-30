/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;


import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static com.daicy.redis.storage.DictKey.safeKey;


public interface Dict {

    int size();

    boolean isEmpty();

    boolean containsKey(DictKey key);

    DictValue get(DictKey key);

    DictValue put(DictKey key, DictValue value);

    DictValue remove(DictKey key);

    void clear();

    Map fork();

    ImmutableSet<DictKey> keySet();

    Set<Map.Entry<DictKey,DictValue>> entrySet();

    default String getString(String key) {
        return getOrDefault(safeKey(key), DictValue.EMPTY_STRING).getString();
    }

    default List<String> getList(String key) {
        return getOrDefault(safeKey(key), DictValue.EMPTY_LIST).getList();
    }

    default Set<String> getSet(String key) {
        return getOrDefault(safeKey(key), DictValue.EMPTY_SET).getSet();
    }

    default CowSortedSet getSortedSet(String key) {
        return getOrDefault(safeKey(key), DictValue.EMPTY_ZSET).getSortedSet();
    }

    default Map<String, String> getHash(String key) {
        return getOrDefault(safeKey(key), DictValue.EMPTY_HASH).getHash();
    }

    default DictValue putIfAbsent(RedisDb db,DictKey key, DictValue value) {
        DictValue oldValue = db.lookupKeyOrExpire(key);
        if (oldValue == null) {
            oldValue = put(key, value);
        }
        return oldValue;
    }

    default DictValue merge(RedisDb db,DictKey key, DictValue value,
                            BiFunction<DictValue, DictValue, DictValue> remappingFunction) {
        DictValue oldValue = db.lookupKeyOrExpire(key);
        DictValue newValue = oldValue == null ? value : remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            remove(key);
        } else {
            put(key, newValue);
        }
        return newValue;
    }

    //
    default DictValue getOrDefault(DictKey key, DictValue defaultValue) {
        DictValue value = get(key);
        return (value != null || containsKey(key)) ? value : defaultValue;
    }

    default boolean rename(DictKey from, DictKey to) {
        DictValue value = remove(from);
        if (value != null) {
            put(to, value);
            return true;
        }
        return false;
    }
}
