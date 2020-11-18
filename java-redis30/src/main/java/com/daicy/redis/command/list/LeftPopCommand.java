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
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;

import static com.daicy.redis.protocal.RedisMessageConstants.NULL;


@Command("lpop")
@ParamLength(1)
@ParamType(DataType.LIST)
public class LeftPopCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        Pair<DictValue, RedisMessage> value =
                db.lookupKeyOrReply(request.getParamStr(0), DataType.LIST, NULL);
        if (null != value.getRight()) {
            return value.getRight();
        }
        LinkedList<String> dictValueList = (LinkedList<String>) value.getLeft().getList();
        if (CollectionUtils.isEmpty(dictValueList)) {
            return NULL;
        }
        return new BulkRedisMessage(dictValueList.removeFirst());
    }
}
