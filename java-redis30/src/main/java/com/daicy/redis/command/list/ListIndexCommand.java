/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.list;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;

import static io.netty.handler.codec.redis.FullBulkStringRedisMessage.NULL_INSTANCE;

@ReadOnly
@Command("lindex")
@ParamLength(2)
@ParamType(DataType.LIST)
public class ListIndexCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        try {
            Pair<DictValue, RedisMessage> value =
                    db.lookupKeyOrReply(request.getParamStr(0), DataType.LIST,NULL_INSTANCE);
            if (null != value.getRight()) {
                return value.getRight();
            }
            LinkedList<String> list = (LinkedList<String>) value.getLeft().getList();

            int index = Integer.parseInt(request.getParamStr(1));
            if (index < 0) {
                index = list.size() + index;
            }
            if (index < 0 || index >= list.size()) {
                return NULL_INSTANCE;
            }
            return new SimpleStringRedisMessage(list.get(index));
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("ERR value is not an integer or out of range");
        } catch (IndexOutOfBoundsException e) {
            return NULL_INSTANCE;
        }
    }
}
