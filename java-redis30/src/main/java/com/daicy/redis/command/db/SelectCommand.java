
package com.daicy.redis.command.db;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.database.Database;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

import static com.daicy.redis.RedisConstants.OK;
import static java.lang.Integer.parseInt;

@ReadOnly
@Command("select")
@ParamLength(1)
public class SelectCommand implements DBCommand {

    @Override
    public RedisMessage execute(Database db, Request request) {
        try {
            request.getClientSession().setDatabaseNum(parseCurrentDB(request));
            return OK;
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("ERR invalid DB index");
        }
    }

    private int parseCurrentDB(Request request) {
        return parseInt(request.getParamStr(0));
    }
}
