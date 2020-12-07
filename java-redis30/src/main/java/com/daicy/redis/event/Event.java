/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.event;


import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class Event {

    private String command;
    private String key;
    private int schema;

    public Event(String command, String key, int schema) {
        this.command = requireNonNull(command);
        this.key = requireNonNull(key);
        this.schema = schema;
    }

    public String getCommand() {
        return command;
    }

    public String getKey() {
        return key;
    }

    public int getSchema() {
        return schema;
    }

    public abstract String getChannel();

    public abstract String getValue();


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Event) {
            if (StringUtils.equals(command, ((Event) obj).command)
                    && StringUtils.equals(key, ((Event) obj).key)
                    && schema == schema) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, key, schema);
    }

    public static KeyEvent keyEvent(String command, String key, int schema) {
        return new KeyEvent(command, key, schema);
    }

    public static KeySpace commandEvent(String command, String key, int schema) {
        return new KeySpace(command, key, schema);
    }
}
