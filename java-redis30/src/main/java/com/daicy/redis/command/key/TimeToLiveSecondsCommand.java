/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.storage.DictValue;

import java.time.Instant;

@Command("ttl")
@ParamLength(1)
public class TimeToLiveSecondsCommand extends TimeToLiveCommand {

  @Override
  protected int timeToLive(DictValue value, Instant now) {
    return value.timeToLiveSeconds(now);
  }
}
