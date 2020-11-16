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
import com.daicy.redis.protocal.BulkReply;
import com.daicy.redis.protocal.ErrorReply;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.protocal.Reply;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;

import static com.daicy.redis.protocal.ReplyConstants.NULL;


@ReadOnly
@Command("lindex")
@ParamLength(2)
@ParamType(DataType.LIST)
public class ListIndexCommand implements DBCommand {

    @Override
    public Reply execute(RedisDb db, Request request) {
        try {
            Pair<DictValue, Reply> value =
                    db.lookupKeyOrReply(request.getParamStr(0), DataType.LIST,NULL);
            if (null != value.getRight()) {
                return value.getRight();
            }
            LinkedList<String> list = (LinkedList<String>) value.getLeft().getList();

            int index = Integer.parseInt(request.getParamStr(1));
            if (index < 0) {
                index = list.size() + index;
            }
            if (index < 0 || index >= list.size()) {
                return NULL;
            }
            return new BulkReply(list.get(index));
        } catch (NumberFormatException e) {
            return new ErrorReply("ERR value is not an integer or out of range");
        } catch (IndexOutOfBoundsException e) {
            return NULL;
        }
    }
}
