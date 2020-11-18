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
import com.daicy.redis.utils.RedisMessageUtils;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;

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
        DictKey dictKey = DictKey.safeKey(request.getParamStr(0));
        List<String> paramsStrList = request.getParamsStrList();
        List<String> removeKeys = paramsStrList.subList(1, paramsStrList.size());
        Set<String> stringSet = db.getDict().getOrDefault(
                dictKey, DictValue.EMPTY_SET).getSet();
        if (CollectionUtils.isEmpty(stringSet)) {
            return NULL;
        }
        Set<String> result = Sets.newHashSet(stringSet);
        for (int i = 0; i < removeKeys.size(); i++) {
            Set<String> removeSet = db.getDict().getOrDefault(
                    DictKey.safeKey(removeKeys.get(0)), DictValue.EMPTY_SET).getSet();
            result.retainAll(removeSet);
        }
        return RedisMessageUtils.toRedisMessage(result);
    }
}
