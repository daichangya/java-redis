/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.hash;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.RedisMessageUtils;

import java.util.Map;

@ReadOnly
@Command("hkeys")
@ParamLength(1)
@ParamType(DataType.HASH)
public class HashKeysCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    Map<String, String> dictValueHash = db.getDict().getHash(request.getParamStr(0));
    return RedisMessageUtils.toRedisMessage(dictValueHash.keySet());
  }
}
