/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.string;


import com.daicy.function.Try;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorReply;
import com.daicy.redis.storage.Dict;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.protocal.Reply;

import static com.daicy.redis.protocal.ReplyConstants.NULL;
import static com.daicy.redis.protocal.ReplyConstants.OK;


/**
 * @author daichangya
 * SET key value [NX] [XX] [EX <seconds>] [PX <milliseconds>]
 * http://redisdoc.com/string/set.html
 */
@Command("set")
@ParamLength(2)
public class SetCommand implements DBCommand {

    @Override
    public Reply execute(RedisDb db, Request request) {
        return Try.of(() -> onSuccess(db.getDict(), request)).recover(this::onFailure)
                .get();
    }

    private Reply onSuccess(Dict db, Request request) {
        DictKey key = DictKey.safeKey(request.getParamStr(0));
        DictValue value = DictValue.string(request.getParamStr(1));
        return value.equals(saveValue(db, key, value)) ? OK : NULL;
    }

    private Reply onFailure(Throwable e) {
        return new ErrorReply("error: " + e.getMessage());
    }

    private DictValue saveValue(Dict db, DictKey key, DictValue value) {
        DictValue savedValue = null;
        savedValue = putValue(db, key, value);
        return savedValue;
    }

    private DictValue putValue(Dict db, DictKey key, DictValue value) {
        db.put(key, value);
        return value;
    }
}
