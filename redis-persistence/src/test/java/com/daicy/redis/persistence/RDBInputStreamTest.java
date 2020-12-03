package com.daicy.redis.persistence;

import com.daicy.redis.storage.DictFactory;
import com.daicy.redis.storage.OnHeapDictFactory;
import com.daicy.redis.storage.RedisDb;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.persistence
 * @date:12/3/20
 */
public class RDBInputStreamTest {

    private final List<RedisDb> databases = new ArrayList<>();


    public void initFactory() {
        DictFactory factory = new OnHeapDictFactory();
        for (int i = 0; i < databases.size(); i++) {
            databases.remove(i);
        }
        for (int i = 0; i < 16; i++) {
            RedisDb redisDb = new RedisDb();
            redisDb.setDict(factory.create());
            redisDb.setExpires(factory.create());
            redisDb.setId(i);
            this.databases.add(redisDb);
        }
    }

    @Test
    public void parse() {
        initFactory();
        File file = new File("dump.rdb");
        try (InputStream fileInputStream = new FileInputStream(file)) {
            RDBInputStream rdb = new RDBInputStream(fileInputStream);
//            byte[] bytes1024 = new byte[10];
//            fileInputStream.read(bytes1024,0,10);
//            System.out.println(CRC64AB.digest(bytes1024));
            rdb.parse(databases);
        } catch (IOException e) {
        }
    }
}