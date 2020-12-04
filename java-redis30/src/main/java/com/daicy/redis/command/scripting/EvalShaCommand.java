/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.scripting;


import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;

@Command("evalsha")
@ParamLength(2)
public class EvalShaCommand extends AbstractEvalCommand {

    @Override
    protected String script(Request request) {
        DefaultRedisServerContext defaultRedisServerContext = request.getServerContext();
        return defaultRedisServerContext.getLuaScripts().get(request.getParamStr(0));
    }
}
