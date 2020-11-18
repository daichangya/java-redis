/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;


import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.BiFunction;


public interface Dict {

    int size();

    boolean isEmpty();

    boolean containsKey(DictKey key);

    DictValue get(DictKey key);

    DictValue put(DictKey key, DictValue value);

    DictValue remove(DictKey key);

    void clear();

    ImmutableSet<DictKey> keySet();

    //  Sequence<DictValue> values();
//
    List<Pair<DictKey, DictValue>> entryList();
//
//  default String getString(String key) {
//    return getOrDefault(safeKey(key), DictValue.EMPTY_STRING).getString();
//  }
//
//  default ImmutableList<String> getList(String key) {
//    return getOrDefault(safeKey(key), DictValue.EMPTY_LIST).getList();
//  }
//
//  default ImmutableSet<String> getSet(String key) {
//    return getOrDefault(safeKey(key), DictValue.EMPTY_SET).getSet();
//  }
//
//  default NavigableSet<Entry<Double, String>> getSortedSet(String key) {
//    return getOrDefault(safeKey(key), DictValue.EMPTY_ZSET).getSortedSet();
//  }
//
//  default ImmutableMap<String, String> getHash(String key) {
//    return getOrDefault(safeKey(key), DictValue.EMPTY_HASH).getHash();
//  }

    //  default void putAll(ImmutableMap<? extends DictKey, ? extends DictValue> map) {
//    map.forEach(this::put);
//  }
//
  default DictValue putIfAbsent(DictKey key, DictValue value) {
    DictValue oldValue = get(key);
    if (oldValue == null) {
        oldValue = put(key, value);
    }
    return oldValue;
  }
//
    default DictValue merge(DictKey key, DictValue value,
                            BiFunction<DictValue, DictValue, DictValue> remappingFunction) {
        DictValue oldValue = get(key);
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

    //
//  default boolean isType(DictKey key, DataType type) {
//    DictValue value = get(key);
//    return value != null ? value.getType() == type : true;
//  }
//
    default boolean rename(DictKey from, DictKey to) {
        DictValue value = remove(from);
        if (value != null) {
            put(to, value);
            return true;
        }
        return false;
    }
//
//  default void overrideAll(ImmutableMap<DictKey, DictValue> value) {
//    clear();
//    putAll(value);
//  }

//  default ImmutableSet<DictKey> evictableKeys(Instant now) {
//    return entrySet()
//        .filter(entry -> entry.get2().isExpired(now))
//        .map(Tuple2::get1);
//  }
}
