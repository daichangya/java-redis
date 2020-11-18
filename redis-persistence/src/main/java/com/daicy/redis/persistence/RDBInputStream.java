/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.persistence;

import com.daicy.redis.persistence.utils.ByteUtils;
import com.daicy.redis.persistence.utils.CRC64;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CheckedInputStream;

import static com.daicy.redis.persistence.utils.ByteUtils.byteArrayToInt;
import static com.daicy.redis.storage.DictValue.list;
import static java.util.Objects.requireNonNull;

public class RDBInputStream {

  private static final String REDIS_PREAMBLE = new String("REDIS");

  private static final long TO_MILLIS = 1000L;

  private static final int HASH = 0x04;
  private static final int SORTED_SET = 0x03;
  private static final int SET = 0x02;
  private static final int LIST = 0x01;
  private static final int STRING = 0x00;

  private static final int TTL_MILLISECONDS = 0xFC;
  private static final int TTL_SECONDS = 0xFD;
  private static final int SELECT = 0xFE;
  private static final int END_OF_STREAM = 0xFF;

  private static final int REDIS_VERSION = 6;
  private static final int VERSION_LENGTH = 4;
  private static final int REDIS_LENGTH = 5;

  private final CheckedInputStream in;

  public RDBInputStream(InputStream in) {
    this.in = new CheckedInputStream(requireNonNull(in), new CRC64());
  }

  public void parse(List<RedisDb> databases) throws IOException {

    int version = version();

    if (version > REDIS_VERSION) {
      throw new IOException("invalid version: " + version);
    }

    Long expireTime = null;
    RedisDb db = null;
    for (boolean end = false; !end;) {
      int read = in.read();
      switch (read) {
      case SELECT:
        db = databases.get(readLength());
        break;
      case TTL_SECONDS:
        expireTime = parseTimeSeconds();
        break;
      case TTL_MILLISECONDS:
        expireTime = parseTimeMillis();
        break;
      case STRING:
        DictKey key = readKey();
        ensure(db, key, DictValue.string(readString()),expireTime);
        expireTime = null;
        break;
      case LIST:
        ensure(db, readKey(), readList(),expireTime);
        expireTime = null;
        break;
//      case SET:
//        ensure(db, readKey(), readSet(expireTime));
//        expireTime = null;
//        break;
//      case SORTED_SET:
//        ensure(db, readKey(), readSortedSet(expireTime));
//        expireTime = null;
//        break;
//      case HASH:
//        ensure(db, readKey(), readHash(expireTime));
//        expireTime = null;
//        break;
      case END_OF_STREAM:
        // end of stream
        end = true;
        db = null;
        expireTime = null;
        break;
      default:
        throw new IOException("not supported: " + read);
      }
    }

    verifyChecksum();

  }

  private long parseTimeSeconds() throws IOException {
    byte[] seconds = read(Integer.BYTES);
    return byteArrayToInt(seconds) * TO_MILLIS;
  }

  private long parseTimeMillis() throws IOException {
    byte[] millis = read(Long.BYTES);
    return ByteUtils.byteArrayToLong(millis);
  }

  private void verifyChecksum() throws IOException {
    long calculated = in.getChecksum().getValue();

    long readed = parseChecksum();

    if (calculated != readed) {
      throw new IOException("invalid checksum: " + readed);
    }
  }

  private long parseChecksum() throws IOException {
    return ByteUtils.byteArrayToLong(read(Long.BYTES));
  }

  private int version() throws IOException {
    String redis = new String(read(REDIS_LENGTH));
    if (!redis.equals(REDIS_PREAMBLE)) {
      throw new IOException("not valid stream");
    }
    return parseVersion(read(VERSION_LENGTH));
  }

  private int parseVersion(byte[] version) {
    StringBuilder sb = new StringBuilder();
    for (byte b : version) {
      sb.append((char) b);
    }
    return Integer.parseInt(sb.toString());
  }

  private DictValue readList() throws IOException {
    int size = readLength();
    List<String> list = new LinkedList<>();
    for (int i = 0; i < size; i++) {
      list.add(readString());
    }
    return list(list);
  }

//  private DictValue readSet(Long expireTime) throws IOException {
//    int size = readLength();
//    Set<String> set = new LinkedHashSet<>();
//    for (int i = 0; i < size; i++) {
//      set.add(readString());
//    }
//    return set(set).expiredAt(expireTime != null ? ofEpochMilli(expireTime) : null);
//  }

//  private DictValue readSortedSet(Long expireTime) throws IOException {
//    int size = readLength();
//    Set<Entry<Double, String>> entries = new LinkedHashSet<>();
//    for (int i = 0; i < size; i++) {
//      String value = readString();
//      Double score = readDouble();
//      entries.add(score(score, value));
//    }
//    return zset(entries).expiredAt(expireTime != null ? ofEpochMilli(expireTime) : null);
//  }
//
//  private DictValue readHash(Long expireTime) throws IOException {
//    int size = readLength();
//    Set<Tuple2<String, String>> entries = new LinkedHashSet<>();
//    for (int i = 0; i < size; i++) {
//      entries.add(entry(readString(), readString()));
//    }
//    return hash(entries).expiredAt(expireTime != null ? ofEpochMilli(expireTime) : null);
//  }

  private void ensure(RedisDb db, DictKey key, DictValue value, Long expireTime) throws IOException {
    if (db != null) {
      db.getDict().put(key, value);
      if (null != expireTime) {
        db.getExpires().put(key, DictValue.toLong(expireTime));
      }
    } else {
      throw new IOException("no database selected");
    }
  }

  private int readLength() throws IOException {
    int length = in.read();
    if (length < 0x40) {
      // 1 byte: 00XXXXXX
      return length;
    } else if (length < 0x80) {
      // 2 bytes: 01XXXXXX XXXXXXXX
      int next = in.read();
      return readLength(length, next);
    } else {
      // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
      return byteArrayToInt(read(Integer.BYTES));
    }
  }

  private int readLength(int length, int next) {
    return ((length & 0x3F) << 8) | (next & 0xFF);
  }

  private String readString() throws IOException {
    int length = readLength();
    return new String(read(length));
  }

  private DictKey readKey() throws IOException {
    return new DictKey(readString());
  }

  private Double readDouble() throws IOException {
    return Double.parseDouble(readString().toString());
  }

  private byte[] read(int size) throws IOException {
    byte[] array = new byte[size];
    int read = in.read(array);
    if (read != size) {
      throw new IOException("error reading stream");
    }
    return array;
  }
}
