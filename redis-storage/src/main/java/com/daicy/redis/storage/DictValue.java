/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;


import com.daicy.collections.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.relaxng.datatype.Datatype;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class DictValue implements Serializable {

    private static final long serialVersionUID = -5178953336530559139L;

    public static final DictValue EMPTY_STRING = string("");
    public static final DictValue EMPTY_LIST = list();
    public static final DictValue EMPTY_SET = set(ImmutableSet.of());
    public static final DictValue EMPTY_ZSET = zset();
    public static final DictValue EMPTY_HASH = hash(ImmutableMap.of());
    public static final DictValue NULL = null;


    private final DataType type;
    private final Object value;

    private DictValue(DataType type, Object value) {
        this.type = requireNonNull(type);
        this.value = requireNonNull(value);
    }

    public DataType getType() {
        return type;
    }

    public Long getLong() {
        requiredType(DataType.LONG);
        return getValue();
    }

    public String getString() {
        requiredType(DataType.STRING);
        return getValue();
    }

    public CowList<String> getList() {
        requiredType(DataType.LIST);
        return getValue();
    }

    public CowSet<String> getSet() {
        requiredType(DataType.SET);
        return getValue();
    }

    public CowSortedSet getSortedSet() {
        requiredType(DataType.ZSET);
        return getValue();
    }

    public CowMap<String, String> getHash() {
        requiredType(DataType.HASH);
        return getValue();
    }

    public DictValue fork() {
        if (DataType.STRING.equals(type) || DataType.LONG.equals(type)) {
            return this;
        } else {
            Forkable forkable = getValue();
            return new DictValue(type, forkable.fork());
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }


    @Override
    public String toString() {
        return "DictValue [type=" + type + ", value=" + value + "]";
    }

    public static DictValue toLong(long value) {
        return new DictValue(DataType.LONG, value);
    }

    public static DictValue string(String value) {
        return new DictValue(DataType.STRING, value);
    }

    public static DictValue list(Collection<String> values) {
        CowList cowList = new CowArrayList();
        cowList.addAll(values);
        return new DictValue(DataType.LIST, cowList);
    }

    public static DictValue list(String... values) {
        return list(Lists.newArrayList(values));
    }

    public static DictValue set(Collection<String> values) {
        CowHashSet cowHashSet = new CowHashSet();
        cowHashSet.addAll(values);
        return new DictValue(DataType.SET, cowHashSet);
    }

    public static DictValue zset(Collection<Entry<Double, String>> values) {
        CowSortedSet cowSortedSet = new CowSortedSet();
        cowSortedSet.addAll(values);
        return new DictValue(DataType.ZSET, cowSortedSet);
    }

    @SafeVarargs
    public static DictValue zset(Entry<Double, String>... values) {
        return zset(Sets.newHashSet(values));
    }


    public static DictValue hash(Map<String, String> values) {
        CowHashMap cowHashMap = new CowHashMap();
        cowHashMap.putAll(values);
        return new DictValue(DataType.HASH, cowHashMap);
    }


    public static Entry<Double, String> score(double score, String value) {
        return new AbstractMap.SimpleEntry<>(score, value);
    }

    public long timeToLive(Instant now) {
        return Duration.between(now, Instant.ofEpochMilli(getLong())).toMillis();
    }

    public Instant getExpiredAt() {
        return Instant.ofEpochMilli(getLong());
    }

    public boolean isExpired(Instant now) {
        Instant expiredAt = getExpiredAt();
        if (expiredAt != null) {
            return now.isAfter(expiredAt);
        }
        return false;
    }

    public long timeToLiveMillis(Instant now) {
        Instant expiredAt = getExpiredAt();
        if (expiredAt != null) {
            return timeToLive(now);
        }
        return -1;
    }

    public int timeToLiveSeconds(Instant now) {
        Instant expiredAt = getExpiredAt();
        if (expiredAt != null) {
            return (int) Math.floorDiv(timeToLive(now), 1000L);
        }
        return -1;
    }


    @SuppressWarnings("unchecked")
    private <T> T getValue() {
        return (T) value;
    }

    private void requiredType(DataType type) {
        if (this.type != type) {
            throw new IllegalStateException("invalid type: " + type);
        }
    }
}
