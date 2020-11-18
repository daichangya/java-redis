package com.daicy.redis;

import com.daicy.redis.command.DBCommandSuite;
import com.daicy.redis.command.RedisCommand;
import com.daicy.redis.context.RedisServerContext;
import com.daicy.redis.persistence.RDBOutputStream;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DictFactory;
import com.daicy.redis.storage.RedisDb;
import com.daicy.remoting.transport.netty4.AbstractServerContext;
import io.netty.channel.Channel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/11/20
 */
public class DefaultRedisServerContext extends AbstractServerContext implements RedisServerContext {

    private final DBCommandSuite commands = new DBCommandSuite();

    private final List<RedisDb> databases = new ArrayList<>();

    private PersistenceManager persistenceManager;

    private final DBConfig dbConfig;

    public DefaultRedisServerContext(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public void init() {
        initFactory();
        initPersistence();
    }

    @Override
    public void start() {
        if (null != persistenceManager) {
            persistenceManager.start();
        }
    }

    @Override
    public void stop() {
        if (null != persistenceManager) {
            persistenceManager.stop();
        }
    }

    private void initFactory() {
        DictFactory factory = ServiceLoaderUtils.loadService(DictFactory.class);
        for (int i = 0; i < dbConfig.getNumDatabases(); i++) {
            RedisDb redisDb = new RedisDb();
            redisDb.setDict(factory.create());
            redisDb.setExpires(factory.create());
            redisDb.setId(i);
            this.databases.add(redisDb);
        }
    }

    private void initPersistence() {
        if (dbConfig.isPersistenceActive()) {
            this.persistenceManager = new PersistenceManager(this, dbConfig);
        }
    }

    public RedisCommand getRedisCommand(String name) {
        return commands.getCommand(name);
    }

    @Override
    public RedisDb getRedisDb(int id) {
        return databases.get(id);
    }

    public List<RedisDb> getDatabases() {
        return databases;
    }

    @Override
    public RedisClientSession newSession(Channel channel) {
        return new RedisClientSession(sourceKey(channel), channel);
    }

    public RedisMessage executeCommand(Request request) {
        RedisCommand redisCommand = getRedisCommand(request.getCommand());
        RedisMessage reply = redisCommand.execute(request);
        propagate(request);
        return reply;
    }

    private void propagate(Request request) {
        if (!isReadOnlyCommand(request.getCommand())) {
            persistenceManager.append(request);
        }
    }

    private boolean isReadOnlyCommand(String command) {
        return commands.isReadOnly(command);
    }

}
