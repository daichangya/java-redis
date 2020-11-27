package com.daicy.collections;

import java.util.Map;

import com.daicy.collections.impl.Beta;

/**
 * A map ordered by keys. This is similar to {@link java.util.NavigableMap} but simpler.
 */
@Beta
public interface OrderedMap<K,V> extends Map<K,V> {
    Iterable<Entry<K,V>> descendingEntries();
    Iterable<Entry<K,V>> descendingEntriesBefore(K upperBoundExclusive,boolean toInclusive);
    Iterable<Entry<K,V>> ascendingSubEntries(K lowerBoundExclusive,boolean fromInclusive,
                                             K upperBoundExclusive,boolean toInclusive);
    default Iterable<Entry<K,V>> ascendingEntries() { return entrySet(); }
    Iterable<Entry<K,V>> ascendingEntriesAfter(K lowerBoundExclusive,boolean fromInclusive);
}
