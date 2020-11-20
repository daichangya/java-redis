package com.daicy.redis.command.db;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.daicy.redis.protocal.RedisMessageConstants.ERR;
import static com.daicy.redis.protocal.RedisMessageConstants.OK;


@Command("save")
public class SaveCommand implements DBCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaveCommand.class);

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        try {
            if (request.getServerContext().isRdbIng()) {
                return new ErrorRedisMessage("Background save already in progress");
            }
            request.getServerContext().exportRDB();
        } catch (IOException e) {
            LOGGER.error("save error", e);
            return ERR;
        }
        return OK;
    }
}
