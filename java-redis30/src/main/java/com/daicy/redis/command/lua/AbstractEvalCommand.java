/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.scripting;


import com.daicy.redis.Request;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

abstract class AbstractEvalCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        String script = script(request);
        if (StringUtils.isEmpty(script)) {
            return new ErrorRedisMessage("NOSCRIPT No matching script. Please use EVAL");
        }
        return execute(request, script);
    }

    private RedisMessage execute(Request request, String script) {
        int numParams = parseInt(request.getParamStr(1));
        if (numParams + 2 > request.getLength()) {
            return new ErrorRedisMessage("invalid number of arguments");
        }
        List<String> params = request.getParamsStrList().stream().skip(2).collect(toList());
        List<String> keys = readParams(numParams, params);
        List<String> argv = readArguments(numParams, params);
        return LuaInterpreter.buildFor(request).execute(script, keys, argv);
    }

    protected abstract String script(Request request);

    private List<String> readParams(int numParams, List<String> params) {
        List<String> keys = new LinkedList<>();
        for (int i = 0; i < numParams; i++) {
            keys.add(params.get(i));
        }
        return keys;
    }

    private List<String> readArguments(int numParams, List<String> params) {
        List<String> argv = new LinkedList<>();
        for (int i = numParams; i < params.size(); i++) {
            argv.add(params.get(i));
        }
        return argv;
    }
}
