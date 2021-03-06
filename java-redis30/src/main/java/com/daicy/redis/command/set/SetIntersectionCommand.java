/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.set;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.client.utils.RedisMessageUtils;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

import static com.daicy.redis.protocal.RedisMessageConstants.NULL;

@ReadOnly
@Command("sinter")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetIntersectionCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        List<String> paramsStrList = request.getParamsStrList();
        List<String> removeKeys = paramsStrList.subList(1, paramsStrList.size());
        Pair<DictValue, RedisMessage> value =
                db.lookupKeyOrReply(request.getParamStr(0),
                        DataType.SET, NULL);
        if (value.getLeft() == null) {
            return value.getRight();
        }
        Set<String> result = Sets.newHashSet(value.getLeft().getSet());
        for (int i = 0; i < removeKeys.size(); i++) {
            Set<String> removeSet = db.lookupKeyOrDefault(removeKeys.get(0),
                    DictValue.EMPTY_SET).getSet();
            result.retainAll(removeSet);
        }
        return RedisMessageUtils.toRedisMessage(result);
    }
}
