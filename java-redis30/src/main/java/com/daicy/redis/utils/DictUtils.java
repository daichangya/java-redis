/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.utils;

import com.daicy.redis.storage.Dict;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.google.common.collect.Lists;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DictUtils {

    public static RedisMessage convertValue(DictValue value) {
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

    public static List<DictValue> getValues(Dict database, List<String> keys) {
        if (CollectionUtils.isEmpty(keys) || null == database) {
            return Lists.newArrayList();
        }
        return keys.stream().map(key -> database.get(new DictKey(key))).collect(Collectors.toList());
    }
}
