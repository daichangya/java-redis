package com.daicy.redis.command.db;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;


@Command("dbsize")
public class DbSizeCommand implements DBCommand {
    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        return new IntegerRedisMessage(db.getDict().size());
    }
}
