package com.daicy.redis;

import com.daicy.redis.database.Database;
import com.daicy.redis.database.OnHeapDatabaseFactory;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/10/20
 */
public class DBServerContext {
    public static final Database database = new OnHeapDatabaseFactory().create("db-1");
}
