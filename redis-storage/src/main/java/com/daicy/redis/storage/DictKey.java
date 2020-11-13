/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;


import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;


public class DictKey implements Comparable<DictKey>, Serializable {

    private static final long serialVersionUID = 7710472090270782053L;

    private final String value;

    public DictKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(DictKey o) {
        return value.compareTo(o.getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            return StringUtils.equals(value, ((DictKey) obj).value);
        }
        return false;
    }


    @Override
    public int hashCode() {
        return Objects.hash(value);
    }


    @Override
    public String toString() {
        return value.toString();
    }

    public static DictKey safeKey(String str) {
        return new DictKey(str);
    }
}
