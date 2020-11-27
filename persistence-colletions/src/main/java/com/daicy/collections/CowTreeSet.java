package com.daicy.collections;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public class CowTreeSet<T> extends AbstractMapBackedSet<T> implements CowSet<T>,OrderedSet<T> {
    private final CowTreeMap<T, Boolean> impl;

    public CowTreeSet(Comparator<T> comparator) {
        this(new CowTreeMap<>(comparator));
    }

    private CowTreeSet(CowTreeMap<T, Boolean> impl) {
        this.impl = impl;
    }

    @Override
    protected CowMap<T, Boolean> backingMap() {
        return impl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CowTreeSet<T> fork() {
        return new CowTreeSet<>(impl.fork());
    }

    @Override
    public Iterable<T> subSet(T lowerBoundExclusive, boolean fromInclusive, T upperBoundExclusive, boolean toInclusive) {
        return () -> new KeyIter(
                impl.ascendingSubEntries(lowerBoundExclusive,fromInclusive,
                        upperBoundExclusive,toInclusive).iterator());
    }

    private class KeyIter implements Iterator {

        private final Iterator<Map.Entry<T, Boolean>> entryIterator;

        private KeyIter(Iterator<Map.Entry<T, Boolean>> entryIterator) {
            this.entryIterator = entryIterator;
        }

        @Override
        public boolean hasNext() {
            return entryIterator.hasNext();
        }

        @Override
        public T next() {
            return entryIterator.next().getKey();
        }
    }

}
