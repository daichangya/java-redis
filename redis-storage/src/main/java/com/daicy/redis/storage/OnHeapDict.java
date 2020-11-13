/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;




import com.google.common.collect.ImmutableSet;

import java.time.Instant;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class OnHeapDict implements Dict {

  private final Map<DictKey, DictValue> cache;

  public OnHeapDict(Map<DictKey, DictValue> cache) {
    this.cache = requireNonNull(cache);
  }

  @Override
  public int size() {
    return cache.size();
  }

  @Override
  public boolean isEmpty() {
    return cache.isEmpty();
  }

  @Override
  public boolean containsKey(DictKey key) {
    return cache.containsKey(key);
  }

  @Override
  public DictValue get(DictKey key) {
    return cache.get(key);
  }

  @Override
  public DictValue put(DictKey key, DictValue value) {
    DictValue oldValue = cache.remove(key);
    cache.put(key, value);
    return oldValue;
  }

  @Override
  public DictValue remove(DictKey key) {
    return cache.remove(key);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public ImmutableSet<DictKey> keySet() {
    return ImmutableSet.copyOf(cache.keySet());
  }
//
//  @Override
//  public Sequence<DictValue> values() {
//    return ImmutableSet.from(cache.values());
//  }
//
//  @Override
//  public ImmutableSet<Tuple2<DictKey, DictValue>> entrySet() {
//    return ImmutableSet.from(cache.entrySet()).map(Tuple::from);
//  }
}
