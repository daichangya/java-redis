
package com.daicy.redis.command.db;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;

import static com.daicy.redis.protocal.RedisMessageConstants.OK;
import static java.lang.Integer.parseInt;

@ReadOnly
@Command("select")
@ParamLength(1)
public class SelectCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        try {
            request.getClientSession().setDictNum(parseCurrentDB(request));
            return OK;
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("ERR invalid DB index");
        }
    }

    private int parseCurrentDB(Request request) {
        return parseInt(request.getParamStr(0));
    }
}
