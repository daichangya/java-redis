/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;


import com.google.common.collect.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class DictValue implements Serializable {

    private static final long serialVersionUID = -5178953336530559139L;

    public static final DictValue EMPTY_STRING = string("");
    public static final DictValue EMPTY_LIST = list();
    public static final DictValue EMPTY_SET = set(Sets.newHashSet());
    public static final DictValue EMPTY_ZSET = zset();
      public static final DictValue EMPTY_HASH = hash();
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

    public List<String> getList() {
        requiredType(DataType.LIST);
        return getValue();
    }

    public Set<String> getSet() {
        requiredType(DataType.SET);
        return getValue();
    }

    public SortedSet getSortedSet() {
        requiredType(DataType.ZSET);
        return getValue();
    }

    public Map<String, String> getHash() {
        requiredType(DataType.HASH);
        return getValue();
    }

//  public int size() {
//    return Pattern1.<Object, Integer>build()
//        .when(instanceOf(Collection.class))
//          .then(collection -> ((Collection<?>) collection).size())
//        .when(instanceOf(Sequence.class))
//          .then(sequence -> ((Sequence<?>) sequence).size())
//        .when(instanceOf(ImmutableMap.class))
//          .then(map -> ((ImmutableMap<?, ?>) map).size())
//        .when(instanceOf(String.class))
//          .returns(1)
//        .otherwise()
//          .returns(0)
//        .apply(this.value);
//  }


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

    public static DictValue list(List<String> values) {
        return new DictValue(DataType.LIST, values);
    }

    public static DictValue list(Collection<String> values) {
        return new DictValue(DataType.LIST, Lists.newLinkedList(values));
    }

    public static DictValue list(String... values) {
        return new DictValue(DataType.LIST, Lists.newLinkedList(Arrays.asList(values)));
    }

    public static DictValue set(Collection<String> values) {
        return new DictValue(DataType.SET, Sets.newHashSet(requireNonNull(values)));
    }

    public static DictValue zset(Collection<Entry<Double, String>> values) {
        return new DictValue(DataType.ZSET,
                requireNonNull(values).stream().collect(collectingAndThen(toSortedSet(),
                        Collections::unmodifiableNavigableSet)));
    }

    @SafeVarargs
    public static DictValue zset(Entry<Double, String>... values) {
        return new DictValue(DataType.ZSET,
                Stream.of(values).collect(collectingAndThen(toSortedSet(),
                        Collections::unmodifiableNavigableSet)));
    }


    public static DictValue hash(Map<String, String> values) {
        return new DictValue(DataType.HASH, values);
    }

    @SafeVarargs
    public static DictValue hash(Pair<String, String>... values) {
        return new DictValue(DataType.HASH,
                Stream.of(values).collect(Collectors.toMap(Pair::getKey, Pair::getValue)));
    }

//  public static DictValue bitset(int... ones) {
//    BitSet bitSet = new BitSet();
//    for (int position : ones) {
//      bitSet.set(position);
//    }
//    return new DictValue(DataType.STRING, new String(bitSet.toByteArray()));
//  }


    public static Entry<Double, String> score(double score, String value) {
        return new AbstractMap.SimpleEntry<>(score, value);
    }

    private static Collector<Entry<Double, String>, ?, NavigableSet<Entry<Double, String>>> toSortedSet() {
        return toCollection(SortedSet::new);
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
