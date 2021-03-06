/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.list;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

import static com.daicy.redis.protocal.RedisMessageConstants.*;


@Command("lset")
@ParamLength(3)
@ParamType(DataType.LIST)
public class ListSetCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        try {
            Pair<DictValue, RedisMessage> value =
                    db.lookupKeyOrReply(request.getParamStr(0), DataType.LIST, NO_KEY);
            if (null != value.getRight()) {
                return value.getRight();
            }
            List<String> list = value.getLeft().getList();
            if (CollectionUtils.isEmpty(list)) {
                return NULL;
            }
            int index = Integer.parseInt(request.getParamStr(1));
            if (index < 0) {
                index = list.size() + index;
            }
            if (index < 0 || index >= list.size()) {
                return OUT_RANGE;
            }
            list.set(index, request.getParamStr(2));
            return OK;
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("ERR value is not an integer or out of range");
        } catch (IndexOutOfBoundsException e) {
            return OUT_RANGE;
        }
    }
}
