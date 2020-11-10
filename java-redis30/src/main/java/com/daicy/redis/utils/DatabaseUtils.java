/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.utils;

import com.daicy.redis.database.DatabaseValue;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

public class DatabaseUtils {

  public static RedisMessage convertValue(DatabaseValue value) {
    if (value != null) {
      switch (value.getType()) {
      case STRING:
          String string = value.getString();
          return new SimpleStringRedisMessage(string);
//      case HASH:
//          ImmutableMap<SafeString, SafeString> map = value.getHash();
//          return array(keyValueList(map).toList());
//      case LIST:
//          ImmutableList<SafeString> list = value.getList();
//          return convertArray(list.toList());
//      case SET:
//          ImmutableSet<SafeString> set = value.getSet();
//          return convertArray(set.toSet());
//      case ZSET:
//          NavigableSet<Entry<Double, SafeString>> zset = value.getSortedSet();
//          return convertArray(serialize(zset));
      default:
        break;
      }
    }
    return FullBulkStringRedisMessage.NULL_INSTANCE;
  }
}
