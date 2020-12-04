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

import static com.daicy.redis.RedisConstants.REDIS_DIRTY_CAS;
import static com.daicy.redis.RedisConstants.REDIS_DIRTY_EXEC;
import static com.daicy.redis.RedisConstants.REDIS_MULTI;
import static com.daicy.redis.protocal.RedisMessageConstants.OK;

@Command("discard")
@TxIgnore
public class DiscardCommand implements DBCommand {


    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        RedisClientSession session = request.getClientSession();

        // 不能在客户端未进行事务状态之前使用
        if ((session.getFlags() & REDIS_MULTI) == 0) {
            return new ErrorRedisMessage("DISCARD without MULTI");
        }

        discardTransaction(session);

        return OK;
    }


    private void discardTransaction(RedisClientSession session) {

        // 重置事务状态
        session.setMultiState(new MultiState());

        // 屏蔽事务状态
        session.setFlags(session.getFlags() & ~(REDIS_MULTI | REDIS_DIRTY_CAS | REDIS_DIRTY_EXEC));

        // 取消对所有键的监视
//    unwatchAllKeys(c);
    }

}
