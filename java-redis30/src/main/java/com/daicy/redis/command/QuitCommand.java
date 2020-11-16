/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.protocal.Reply;
import com.daicy.redis.protocal.ReplyConstants;

@Command("quit")
public class QuitCommand implements RedisCommand {

  @Override
  public Reply execute(Request request) {
    return ReplyConstants.OK;
  }

}
