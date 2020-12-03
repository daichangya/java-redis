/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.daicy.redis.command.db;


import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.ReplicationManager;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.protocal.StatusRedisMessage;
import com.daicy.redis.storage.RedisDb;
import org.apache.commons.lang3.StringUtils;

import static com.daicy.redis.protocal.RedisMessageConstants.OK;

@ReadOnly
@Command("slaveof")
@ParamLength(2)
public class SlaveOfCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        String host = request.getParamStr(0);
        String port = request.getParamStr(1);
        DefaultRedisServerContext redisServerContext = request.getServerContext();

        boolean stopCurrent = "NO".equals(host) && "ONE".equals(port);
        if (stopCurrent) {
            if(StringUtils.isNotBlank(redisServerContext.getMasterhost())){
                 ReplicationManager.replicationUnsetMaster();
            }
        } else {
            if (StringUtils.equals(host, redisServerContext.getMasterhost())
                    && StringUtils.equals(port, redisServerContext.getMasterport())) {
                return new StatusRedisMessage("OK Already connected to specified master");
            } else {
                ReplicationManager.replicationSetMaster(host, port);
            }
        }

        return OK;
    }

}
