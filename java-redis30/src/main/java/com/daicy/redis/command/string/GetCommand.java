/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.daicy.redis.command.string;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.Dict;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.RedisDb;
import io.netty.handler.codec.redis.RedisMessage;

import static com.daicy.redis.utils.DictUtils.convertValue;

@Command("get")
@ParamLength(1)
public class GetCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    return convertValue(db.getDict().get(DictKey.safeKey(request.getParamStr(0))));
  }
}
