/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;


import com.daicy.collections.CowHashMap;
import com.daicy.collections.CowMap;
import com.daicy.collections.CowTreeSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public class CowSortedSet implements Set<Entry<Double, String>>, Serializable {

    private static final long serialVersionUID = -2221385877842299451L;

    private transient CowMap<String, Double> items = new CowHashMap<>();

    private transient CowTreeSet<Entry<Double, String>> scores = new CowTreeSet<>(this::compare);


    public CowSortedSet(CowMap<String, Double> items, CowTreeSet<Entry<Double, String>> scores) {
        this.items = items;
        this.scores = scores;
    }

    public CowSortedSet() {
    }

    @Override
    public int size() {
        return scores.size();
    }

    @Override
    public boolean isEmpty() {
        return scores.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        if (o instanceof Entry) {
            Entry<Double, String> entry = Entry.class.cast(o);
            return items.containsKey(entry.getValue());
        }
        return false;
    }

    @Override
    public Iterator<Entry<Double, String>> iterator() {
        return scores.iterator();
    }

    @Override
    public Object[] toArray() {
        return scores.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return scores.toArray(a);
    }

    @Override
    public boolean add(Entry<Double, String> e) {
        if (!items.containsKey(e.getValue())) {
            items.put(e.getValue(), e.getKey());
            scores.add(e);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        if (o instanceof Entry) {
            Entry<Double, String> entry = Entry.class.cast(o);
            if (items.containsKey(entry.getValue())) {
                double score = items.remove(entry.getValue());
                scores.remove(DictValue.score(score, entry.getValue()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean result = false;
        for (Object object : c) {
            result |= contains(object);
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends Entry<Double, String>> c) {
        boolean result = false;
        for (Entry<Double, String> entry : c) {
            result |= add(entry);
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Set<String> toRemove = new HashSet<>(items.keySet());
        toRemove.removeAll(c);
        boolean result = false;
        for (String key : toRemove) {
            result |= remove(DictValue.score(0, key));
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for (Object object : c) {
            result |= remove(object);
        }
        return result;
    }

    @Override
    public void clear() {
        items.clear();
        scores.clear();
    }


    public double score(String key) {
        Double score = items.get(key);
        return null == score ? 0 : score;
    }

    public CowSortedSet fork() {
        return new CowSortedSet(items.fork(), scores.fork());
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Set) {
            Set<?> other = (Set<?>) obj;
            if (scores != null) {
                return scores.equals(other);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return scores.toString();
    }

    private int compare(Entry<Double, String> o1, Entry<Double, String> o2) {
        int key = o1.getKey().compareTo(o2.getKey());
        if (key != 0) {
            return key;
        }
        if (StringUtils.isEmpty(o1.getValue())) {
            return 0;
        }
        if (StringUtils.isEmpty(o2.getValue())) {
            return 0;
        }
        return o1.getValue().compareTo(o2.getValue());
    }

    public Set<Entry<Double, String>> subSet(Entry<Double, String> fromElement, boolean fromInclusive,
                                             Entry<Double, String> toElement, boolean toInclusive) {
        TreeSet<Entry<Double, String>> treeSet = new TreeSet<Entry<Double, String>>(this::compare);
        Iterables.addAll(treeSet, scores.subSet(fromElement, fromInclusive, toElement, toInclusive));
        return treeSet;
    }
}
