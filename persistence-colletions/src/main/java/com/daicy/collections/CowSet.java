package com.daicy.collections;

import java.util.Set;

/**
 * {@inheritDoc}
 */
public interface CowSet<E> extends Set<E>, CowCollection<E> {
    /**
     * {@inheritDoc}
     */
    @Override
    CowSet<E> fork();
}
