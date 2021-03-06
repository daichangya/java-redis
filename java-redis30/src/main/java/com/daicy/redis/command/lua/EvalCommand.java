/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.lua;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;

@Command("eval")
@ParamLength(2)
public class EvalCommand extends AbstractEvalCommand {

  @Override
  protected String script(Request request) {
    return request.getParamStr(0);
  }
}
