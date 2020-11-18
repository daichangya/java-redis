/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.persistence;


import com.daicy.redis.persistence.utils.ByteUtils;
import com.daicy.redis.persistence.utils.CRC64;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.zip.CheckedOutputStream;

import static java.util.Objects.requireNonNull;

public class RDBOutputStream {

    private static final byte[] REDIS = "REDIS".getBytes();

    private static final int TTL_MILLISECONDS = 0xFC;
    private static final int END_OF_STREAM = 0xFF;
    private static final int SELECT = 0xFE;

    private final CheckedOutputStream out;

    public RDBOutputStream(OutputStream out) {
        this.out = new CheckedOutputStream(requireNonNull(out), new CRC64());
    }

    public void preamble(int version) throws IOException {
        out.write(REDIS);
        out.write(version(version));
    }

    private byte[] version(int version) {
        StringBuilder sb = new StringBuilder(String.valueOf(version));
        for (int i = sb.length(); i < Integer.BYTES; i++) {
            sb.insert(0, '0');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void select(int db) throws IOException {
        out.write(SELECT);
        length(db);
    }

    public void dabatase(RedisDb db) throws IOException {
        for (Pair<DictKey, DictValue> entry : db.getDict().entryList()) {
            value(entry.getKey(), entry.getValue(), db.getExpires().get(entry.getKey()));
        }
    }

    private void value(DictKey key, DictValue value, DictValue expired) throws IOException {
        expiredAt(expired);
        type(value.getType());
        key(key);
        value(value);
    }

    private void expiredAt(DictValue expired) throws IOException {
        if (expired != null) {
            out.write(TTL_MILLISECONDS);
            out.write(ByteUtils.toByteArray(expired.getExpiredAt().toEpochMilli()));
        }
    }

    private void type(DataType type) throws IOException {
        out.write(type.getValue());
    }

    private void key(DictKey key) throws IOException {
        string(key.getValue());
    }

    private void value(DictValue value) throws IOException {
        switch (value.getType()) {
            case STRING:
                string(value.getString());
                break;
            case LIST:
                list(value.getList());
                break;
//    case HASH:
//      hash(value.getHash());
//      break;
//    case SET:
//      set(value.getSet());
//      break;
//    case ZSET:
//      zset(value.getSortedSet());
//      break;
            default:
                break;
        }
    }


    private void length(int length) throws IOException {
        if (length < 0x40) {
            // 1 byte: 00XXXXXX
            out.write(length);
        } else if (length < 0x4000) {
            // 2 bytes: 01XXXXXX XXXXXXXX
            int b1 = length >> 8;
            int b2 = length & 0xFF;
            out.write(0x40 | b1);
            out.write(b2);
        } else {
            // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
            out.write(0x80);
            out.write(ByteUtils.toByteArray(length));
        }
    }

    private void string(String value) throws IOException {
        byte[] bytes = value.getBytes();
        length(bytes.length);
        out.write(bytes);
    }

    private void string(double value) throws IOException {
        string(String.valueOf(value));
    }

    private void list(List<String> value) throws IOException {
        length(value.size());
        for (String item : value) {
            string(item);
        }
    }

//  private void hash(ImmutableMap<String, String> value) throws IOException {
//    length(value.size());
//    for (Tuple2<String, String> entry : value.entries()) {
//      string(entry.get1());
//      string(entry.get2());
//    }
//  }
//
//  private void set(ImmutableSet<String> value) throws IOException {
//    length(value.size());
//    for (String item : value) {
//      string(item);
//    }
//  }
//
//  private void zset(NavigableSet<Entry<Double, String>> value) throws IOException {
//    length(value.size());
//    for (Entry<Double, String> item : value) {
//      string(item.getValue());
//      string(item.getKey());
//    }
//  }

    public void end() throws IOException {
        out.write(END_OF_STREAM);
        out.write(ByteUtils.toByteArray(out.getChecksum().getValue()));
        out.flush();
    }
}
