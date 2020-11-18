/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;


import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        return cache.put(key, value);
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
    @Override
    public List<Pair<DictKey, DictValue>> entryList() {
        return cache.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
}
