package com.daicy.collections;

import com.daicy.collections.impl.Beta;

/**
 * {@inheritDoc}
 */
@Beta
public interface CowOrderedMap<K,V> extends CowMap<K,V>, OrderedMap<K,V> {
    /**
     * {@inheritDoc}
     */
    @Override CowOrderedMap<K,V> fork();
}
