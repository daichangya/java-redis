package com.daicy.redis.command.db;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.protocal.StatusRedisMessage;
import com.daicy.redis.storage.RedisDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Command("bgsave")
public class BgSaveCommand implements DBCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(BgSaveCommand.class);

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        if (request.getServerContext().isRdbIng()) {
            return new ErrorRedisMessage("Background save already in progress");
        }
        request.getServerContext().exportRDBBg();
        return new StatusRedisMessage("Background saving started");
    }
}
