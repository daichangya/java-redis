/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;


import com.daicy.redis.RedisServerContext;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.*;
import com.daicy.redis.protocal.ErrorReply;
import com.daicy.redis.protocal.Reply;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.RedisDb;

public class DBCommandWrapper implements RedisCommand {

    private int params;

    private DataType dataType;

    private final boolean pubSubAllowed;
    private final boolean txIgnore;
    private final boolean readOnly;

    private final Object command;

    public DBCommandWrapper(Object command) {
        this.command = command;
        ParamLength length = command.getClass().getAnnotation(ParamLength.class);
        if (length != null) {
            this.params = length.value();
        }
        ParamType type = command.getClass().getAnnotation(ParamType.class);
        if (type != null) {
            this.dataType = type.value();
        }
        this.readOnly = command.getClass().isAnnotationPresent(ReadOnly.class);
        this.txIgnore = command.getClass().isAnnotationPresent(TxIgnore.class);
        this.pubSubAllowed = command.getClass().isAnnotationPresent(PubSubAllowed.class);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isTxIgnore() {
        return txIgnore;
    }

    public boolean isPubSubAllowed() {
        return pubSubAllowed;
    }

    @Override
    public Reply execute(Request request) {
        // FIXME: ugly piece of code, please refactor
        RedisDb db = RedisServerContext.getInstance().getRedisDb(
                request.getClientSession().getDictNum());
        if (request.getLength() < params) {
            return new ErrorReply("ERR wrong number of arguments for '" + request.getCommand() + "' command");
        }
        if (command instanceof DBCommand) {
            return executeDBCommand(db, request);
        } else if (command instanceof RedisCommand) {
            return executeCommand(request);
        }
        return new ErrorReply("invalid command type: " + command.getClass());
    }

    private Reply executeCommand(Request request) {
        return ((RedisCommand) command).execute(request);
    }

    private Reply executeDBCommand(RedisDb db, Request request) {
        return ((DBCommand) command).execute(db, request);
    }

}
