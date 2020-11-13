/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.key;


import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.utils.DictUtils;

@Command("pexpire")
@ParamLength(2)
public class PExpireCommand extends ExpireCommand {

    @Override
    protected long parsetTtl(String param) {
        return DictUtils.toInstantSs(Long.parseLong(param)).toEpochMilli();
    }
}
