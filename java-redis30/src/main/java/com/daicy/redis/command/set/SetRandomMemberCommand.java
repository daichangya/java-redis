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
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.protocal.RedisMessageConstants;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.RedisMessageUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

@ReadOnly
@Command("srandmember")
@ParamLength(1)
@ParamType(DataType.SET)
public class SetRandomMemberCommand implements DBCommand {


    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        DictKey dictKey = DictKey.safeKey(request.getParamStr(0));
        Set<String> stringSet = db.getDict().getOrDefault(
                dictKey, DictValue.EMPTY_SET).getSet();
        if (request.getParamsStrList().size() == 1) {
            if (CollectionUtils.isEmpty(stringSet)) {
                return RedisMessageConstants.NULL;
            }
            String result = stringSet.iterator().next();
            return new BulkRedisMessage(result);
        } else {
            int count = Integer.parseInt(request.getParamStr(1));
            int size = stringSet.size();
            if (count < 0) {
                count = Math.abs(count);
            } else if (count > size) {
                count = size;
            }
            List<String> resultList = Lists.newArrayList();
            Iterator<String> iterator = stringSet.iterator();
            while (count > 0) {
                if (iterator.hasNext()) {
                    resultList.add(iterator.next());
                } else {
                    iterator = stringSet.iterator();
                }
                count--;
            }
            return RedisMessageUtils.toRedisMessage(resultList);
        }
    }
}
