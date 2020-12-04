/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.transaction;


import com.daicy.redis.RedisClientSession;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.TxIgnore;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;

import static com.daicy.redis.RedisConstants.REDIS_MULTI;
import static com.daicy.redis.protocal.RedisMessageConstants.OK;

@Command("multi")
@TxIgnore
public class MultiCommand implements DBCommand {


    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        RedisClientSession session = request.getClientSession();
        // 不能在事务中嵌套事务
        if ((session.getFlags() & REDIS_MULTI) > 0) {
            return new ErrorRedisMessage("MULTI calls can not be nested");
        }

        // 打开事务 FLAG
        session.setFlags(session.getFlags() | REDIS_MULTI);
        createTransaction(session);
        return OK;
    }

    private void createTransaction(RedisClientSession session) {
        session.setMultiState(new MultiState());
    }

}
