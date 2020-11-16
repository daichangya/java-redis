/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.list;


import com.daicy.redis.Request;
import com.daicy.redis.ServiceLoaderUtils;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorReply;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.utils.DictUtils;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import com.daicy.redis.protocal.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.daicy.redis.storage.DictKey.safeKey;

@ReadOnly
@Command("lrange")
@ParamLength(3)
@ParamType(DataType.LIST)
public class ListRangeCommand implements DBCommand {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public Reply execute(RedisDb db, Request request) {
        try {
            DictValue value = db.getDict().getOrDefault(safeKey(request.getParamStr(0)), DictValue.EMPTY_LIST);
            List<String> list = value.getList();

            int from = Integer.parseInt(request.getParamStr(1));
            if (from < 0) {
                from = list.size() + from;
            }
            int to = Integer.parseInt(request.getParamStr(2));
            if (to < 0) {
                to = list.size() + to;
            }

            int min = Math.min(from, to);
            int max = Math.max(from, to);

            // TODO: use Array
            List<String> result = list.stream().skip(min).limit((max - min) + 1).collect(Collectors.toList());

            return DictUtils.toRedisMessage(result);
        } catch (NumberFormatException e) {
            LOG.error("ListRangeCommand error :", e);
            return new ErrorReply("ERR value is not an integer or out of range");
        }
    }
}
