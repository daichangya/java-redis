/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;


import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public class SortedSet implements NavigableSet<Entry<Double, String>>, Serializable {

    private static final long serialVersionUID = -2221385877842299451L;

    private transient Map<String, Double> items = new HashMap<>();

    private transient NavigableSet<Entry<Double, String>> scores = new TreeSet<>(this::compare);

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

    @Override
    public Comparator<? super Entry<Double, String>> comparator() {
        return scores.comparator();
    }

    @Override
    public Entry<Double, String> first() {
        return scores.first();
    }

    @Override
    public Entry<Double, String> last() {
        return scores.last();
    }

    @Override
    public Entry<Double, String> lower(Entry<Double, String> e) {
        return scores.lower(e);
    }

    @Override
    public Entry<Double, String> floor(Entry<Double, String> e) {
        return scores.floor(e);
    }

    @Override
    public Entry<Double, String> ceiling(Entry<Double, String> e) {
        return scores.ceiling(e);
    }

    @Override
    public Entry<Double, String> higher(Entry<Double, String> e) {
        return scores.higher(e);
    }

    @Override
    public Entry<Double, String> pollFirst() {
        return scores.pollFirst();
    }

    @Override
    public Entry<Double, String> pollLast() {
        return scores.pollLast();
    }

    @Override
    public NavigableSet<Entry<Double, String>> descendingSet() {
        return scores.descendingSet();
    }

    @Override
    public Iterator<Entry<Double, String>> descendingIterator() {
        return scores.descendingIterator();
    }

    @Override
    public NavigableSet<Entry<Double, String>> subSet(Entry<Double, String> fromElement,
                                                      boolean fromInclusive, Entry<Double, String> toElement, boolean toInclusive) {
        return scores.subSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public NavigableSet<Entry<Double, String>> headSet(Entry<Double, String> toElement,
                                                       boolean inclusive) {
        return scores.headSet(toElement, inclusive);
    }

    @Override
    public NavigableSet<Entry<Double, String>> tailSet(Entry<Double, String> fromElement,
                                                       boolean inclusive) {
        return scores.tailSet(fromElement, inclusive);
    }

    @Override
    public java.util.SortedSet<Entry<Double, String>> subSet(Entry<Double, String> fromElement,
                                                             Entry<Double, String> toElement) {
        return scores.subSet(fromElement, toElement);
    }

    @Override
    public java.util.SortedSet<Entry<Double, String>> headSet(Entry<Double, String> toElement) {
        return scores.headSet(toElement);
    }

    @Override
    public java.util.SortedSet<Entry<Double, String>> tailSet(Entry<Double, String> fromElement) {
        return scores.tailSet(fromElement);
    }

    public double score(String key) {
        Double score = items.get(key);
        return null == score ? 0 : score;
    }

    public int ranking(String key) {
        if (items.containsKey(key)) {
            double score = items.get(key);

            Set<Entry<Double, String>> head = scores.headSet(DictValue.score(score, key));

            return head.size();
        }
        return -1;
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

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(items);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        Map<String, Double> entries = (Map<String, Double>) input.readObject();
        this.items = new HashMap<>();
        this.scores = new TreeSet<>(this::compare);
        for (Entry<String, Double> entry : entries.entrySet()) {
            items.put(entry.getKey(), entry.getValue());
            scores.add(new AbstractMap.SimpleEntry<>(entry.getValue(), entry.getKey()));
        }
    }
}
