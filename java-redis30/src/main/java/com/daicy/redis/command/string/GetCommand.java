/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.daicy.redis.command.string;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.database.Database;
import com.daicy.redis.database.DatabaseKey;
import io.netty.handler.codec.redis.RedisMessage;

import static com.daicy.redis.utils.DatabaseUtils.convertValue;

@Command("get")
@ParamLength(1)
public class GetCommand implements DBCommand {

  @Override
  public RedisMessage execute(Database db, Request request) {
    return convertValue(db.get(DatabaseKey.safeKey(request.getParamStr(0))));
  }
}
