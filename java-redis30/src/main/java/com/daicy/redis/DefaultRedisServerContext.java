package com.daicy.redis;

import com.daicy.redis.command.DBCommandSuite;
import com.daicy.redis.command.RedisCommand;
import com.daicy.redis.context.DBConfig;
import com.daicy.redis.context.RedisServerContext;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DictFactory;
import com.daicy.redis.storage.RedisDb;
import com.daicy.remoting.transport.netty4.AbstractServerContext;
import com.daicy.remoting.transport.netty4.ClientSession;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.daicy.redis.RedisConstants.REDIS_REPL_NONE;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/11/20
 */
public class DefaultRedisServerContext extends AbstractServerContext implements RedisServerContext {

    private final ConcurrentSkipListSet<ClientSession> slaves = new ConcurrentSkipListSet<>();

    private static DefaultRedisServerContext instance;

    private RedisClientSession master;

    private SlaveRedisClient slaveRedisClient;

    private String masterhost;

    private String masterport;

    // 复制的状态（服务器是从服务器时使用）
    private volatile int repl_state = REDIS_REPL_NONE;          /* ReplicationManager status if the instance is a slave */

    private final DBCommandSuite commands = new DBCommandSuite();

    private final List<RedisDb> databases = new ArrayList<>();

    private Map<String,String> luaScripts = Maps.newHashMap();

    private PersistenceManager persistenceManager;

    private final DBConfig dbConfig;

    private volatile boolean isRdbIng = false;

    private volatile boolean isAofIng = false;


    public DefaultRedisServerContext(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        instance = this;
    }

    public static DefaultRedisServerContext getInstance(){
        return instance;
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

    public void initFactory() {
        DictFactory factory = ServiceLoaderUtils.loadService(DictFactory.class);
        for (int i = 0; i < databases.size(); i++) {
            databases.remove(i);
        }
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
            ReplicationManager.replicationFeedSlaves(request);
        }
    }


    public void processCommand(RedisMessage redisMessage) {
        if (null != persistenceManager) {
            persistenceManager.processCommand(redisMessage);
        }
    }


    public void exportRDBBg() {
        if (null != persistenceManager) {
            persistenceManager.exportRDBBg();
        }
    }


    public void importRDB(File file) {
        if (null != persistenceManager) {
            persistenceManager.importRDB(file);
        }
    }

    public void exportRDB() {
        if (null != persistenceManager) {
            persistenceManager.exportRDB();
        }
    }

    private boolean isReadOnlyCommand(String command) {
        return commands.isReadOnly(command);
    }

    public boolean isRdbIng() {
        return isRdbIng;
    }

    public void setRdbIng(boolean rdbIng) {
        isRdbIng = rdbIng;
    }

    public boolean isAofIng() {
        return isAofIng;
    }

    public void setAofIng(boolean aofIng) {
        isAofIng = aofIng;
    }

    public ConcurrentSkipListSet<ClientSession> getSlaves() {
        return slaves;
    }

    public RedisClientSession getMaster() {
        return master;
    }

    public void setMaster(RedisClientSession master) {
        this.master = master;
    }

    public String getMasterhost() {
        return masterhost;
    }

    public void setMasterhost(String masterhost) {
        this.masterhost = masterhost;
    }

    public String getMasterport() {
        return masterport;
    }

    public void setMasterport(String masterport) {
        this.masterport = masterport;
    }

    public int getRepl_state() {
        return repl_state;
    }

    public void setRepl_state(int repl_state) {
        this.repl_state = repl_state;
    }

    @Override
    public DBConfig getDbConfig() {
        return dbConfig;
    }

    public SlaveRedisClient getSlaveRedisClient() {
        return slaveRedisClient;
    }

    public void setSlaveRedisClient(SlaveRedisClient slaveRedisClient) {
        this.slaveRedisClient = slaveRedisClient;
    }

    public Map<String, String> getLuaScripts() {
        return luaScripts;
    }

    public void setLuaScripts(Map<String, String> luaScripts) {
        this.luaScripts = luaScripts;
    }
}
