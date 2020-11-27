package com.daicy.collections;

import com.daicy.collections.impl.Beta;

import java.util.Map;
import java.util.Set;

/**
 * A map ordered by keys. This is similar to {@link java.util.NavigableMap} but simpler.
 */
@Beta
public interface OrderedSet<K> extends Set<K> {
    Iterable<K> subSet(K lowerBoundExclusive, boolean fromInclusive,
                                             K upperBoundExclusive, boolean toInclusive);
}
