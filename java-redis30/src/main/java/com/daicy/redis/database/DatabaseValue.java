/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.database;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static com.google.common.base.Predicates.instanceOf;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class DatabaseValue implements Serializable {

  private static final long serialVersionUID = -5178953336530559139L;

  public static final DatabaseValue EMPTY_STRING = string("");
//  public static final DatabaseValue EMPTY_LIST = list();
//  public static final DatabaseValue EMPTY_SET = set();
//  public static final DatabaseValue EMPTY_ZSET = zset();
//  public static final DatabaseValue EMPTY_HASH = hash();
  public static final DatabaseValue NULL = null;


  private final DataType type;
  private final Object value;
  private final Instant expiredAt;

  private DatabaseValue(DataType type, Object value) {
    this(type, value, null);
  }

  private DatabaseValue(DataType type, Object value, Instant expiredAt) {
    this.type = requireNonNull(type);
    this.value = requireNonNull(value);
    this.expiredAt = expiredAt;
  }

  public DataType getType() {
    return type;
  }

  public String getString() {
    requiredType(DataType.STRING);
    return getValue();
  }

  public ImmutableList<String> getList() {
    requiredType(DataType.LIST);
    return getValue();
  }

  public ImmutableSet<String> getSet() {
    requiredType(DataType.SET);
    return getValue();
  }

  public NavigableSet<Entry<Double, String>> getSortedSet() {
    requiredType(DataType.ZSET);
    return getValue();
  }

  public ImmutableMap<String, String> getHash() {
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

  public Instant getExpiredAt() {
    return expiredAt;
  }

  public boolean isExpired(Instant now) {
    if (expiredAt != null) {
      return now.isAfter(expiredAt);
    }
    return false;
  }

  public long timeToLiveMillis(Instant now) {
    if (expiredAt != null) {
      return timeToLive(now);
    }
    return -1;
  }

  public int timeToLiveSeconds(Instant now) {
    if (expiredAt != null) {
      return (int) Math.floorDiv(timeToLive(now), 1000L);
    }
    return -1;
  }

  public DatabaseValue expiredAt(Instant instant) {
    return new DatabaseValue(this.type, this.value, instant);
  }

  public DatabaseValue expiredAt(int ttlSeconds) {
    return new DatabaseValue(this.type, this.value, toInstant(toMillis(ttlSeconds)));
  }

  public DatabaseValue noExpire() {
    return new DatabaseValue(this.type, this.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value);
  }


  @Override
  public String toString() {
    return "DatabaseValue [type=" + type + ", value=" + value + "]";
  }


  public static DatabaseValue string(String value) {
    return new DatabaseValue(DataType.STRING, value);
  }

//  public static DatabaseValue list(Sequence<String> values) {
//    return new DatabaseValue(DataType.LIST, values.asList());
//  }
//
//  public static DatabaseValue list(Collection<String> values) {
//    return new DatabaseValue(DataType.LIST, ImmutableList.from(requireNonNull(values).stream()));
//  }
//
//  public static DatabaseValue list(String... values) {
//    return new DatabaseValue(DataType.LIST, ImmutableList.from(Stream.of(values)));
//  }
//
//  public static DatabaseValue set(Sequence<String> values) {
//    return new DatabaseValue(DataType.SET, values.asSet());
//  }
//
//  public static DatabaseValue set(Collection<String> values) {
//    return new DatabaseValue(DataType.SET, ImmutableSet.from(requireNonNull(values).stream()));
//  }
//
//  public static DatabaseValue set(String... values) {
//    return new DatabaseValue(DataType.SET, ImmutableSet.from(Stream.of(values)));
//  }
//
//  public static DatabaseValue zset(Collection<Entry<Double, String>> values) {
//    return new DatabaseValue(DataType.ZSET,
//        requireNonNull(values).stream().collect(collectingAndThen(toSortedSet(),
//                                                                  Collections::unmodifiableNavigableSet)));
//  }
//
//  @SafeVarargs
//  public static DatabaseValue zset(Entry<Double, String>... values) {
//    return new DatabaseValue(DataType.ZSET,
//        Stream.of(values).collect(collectingAndThen(toSortedSet(),
//                                                    Collections::unmodifiableNavigableSet)));
//  }
//
//  public static DatabaseValue hash(ImmutableMap<String, String> values) {
//    return new DatabaseValue(DataType.HASH, values);
//  }
//
//  public static DatabaseValue hash(Collection<Tuple2<String, String>> values) {
//    return new DatabaseValue(DataType.HASH, ImmutableMap.from(requireNonNull(values).stream()));
//  }
//
//  public static DatabaseValue hash(Sequence<Tuple2<String, String>> values) {
//    return new DatabaseValue(DataType.HASH, ImmutableMap.from(requireNonNull(values).stream()));
//  }
//
//  @SafeVarargs
//  public static DatabaseValue hash(Tuple2<String, String>... values) {
//    return new DatabaseValue(DataType.HASH, ImmutableMap.from(Stream.of(values)));
//  }
//
//  public static DatabaseValue bitset(int... ones) {
//    BitSet bitSet = new BitSet();
//    for (int position : ones) {
//      bitSet.set(position);
//    }
//    return new DatabaseValue(DataType.STRING, new String(bitSet.toByteArray()));
//  }
//
//  public static Tuple2<String, String> entry(String key, String value) {
//    return Tuple.of(key, value);
//  }
//
//  public static Entry<Double, String> score(double score, String value) {
//    return new SimpleEntry<>(score, value);
//  }
//
//  private static Collector<Entry<Double, String>, ?, NavigableSet<Entry<Double, String>>> toSortedSet() {
//    return toCollection(SortedSet::new);
//  }

  private long timeToLive(Instant now) {
    return Duration.between(now, expiredAt).toMillis();
  }

  private Instant toInstant(long ttlMillis) {
    return now().plusMillis(ttlMillis);
  }

  private long toMillis(int ttlSeconds) {
    return TimeUnit.SECONDS.toMillis(ttlSeconds);
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
