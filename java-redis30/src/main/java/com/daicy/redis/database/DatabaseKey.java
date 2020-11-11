/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.database;


import io.netty.buffer.DefaultByteBufHolder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;


public class DatabaseKey implements Comparable<DatabaseKey>, Serializable {

    private static final long serialVersionUID = 7710472090270782053L;

    private final String value;

    public DatabaseKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(DatabaseKey o) {
        return value.compareTo(o.getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            return StringUtils.equals(value, ((DatabaseKey) obj).value);
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

    public static DatabaseKey safeKey(String str) {
        return new DatabaseKey(str);
    }

}
