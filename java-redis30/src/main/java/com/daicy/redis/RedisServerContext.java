package com.daicy.redis;

import com.daicy.redis.command.CommandSuite;
import com.daicy.redis.command.DBCommandSuite;
import com.daicy.redis.command.RedisCommand;
import com.daicy.redis.database.Database;
import com.daicy.redis.database.DatabaseFactory;
import com.daicy.remoting.transport.netty4.AbstractServerContext;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/11/20
 */
public class RedisServerContext extends AbstractServerContext {

    private final CommandSuite commands = new DBCommandSuite();

    private final List<Database> databases = new ArrayList<>();

    private static RedisServerContext ourInstance = new RedisServerContext();

    public static RedisServerContext getInstance() {
        return ourInstance;
    }

    private RedisServerContext() {
        DatabaseFactory factory = ServiceLoaderUtils.loadService(DatabaseFactory.class);
        for (int i = 0; i < 16; i++) {
            this.databases.add(factory.create("db-" + i));
        }
    }

    public RedisCommand getRedisCommand(String name) {
        return commands.getCommand(name);
    }

    public Database getDatabase(int id) {
        return databases.get(id);
    }

    @Override
    protected RedisClientSession newSession(Channel channel) {
        return new RedisClientSession(sourceKey(channel),channel);
    }
}
