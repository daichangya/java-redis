package com.daicy.redis.persistence;

import com.daicy.redis.storage.DictFactory;
import com.daicy.redis.storage.OnHeapDictFactory;
import com.daicy.redis.storage.RedisDb;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.daicy.redis.persistence.RdbConstants.*;

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
            System.out.println(e);
        }
    }

    @Test
    public void testZZ(){
        System.out.println((REDIS_RDB_ENCVAL<<6));
        System.out.println((REDIS_RDB_ENCVAL<<6)|REDIS_RDB_ENC_INT8);
        System.out.println((REDIS_RDB_ENCVAL<<6)|REDIS_RDB_ENC_INT16);
        System.out.println((1<<31)-1);
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Ints.stringConverter().convert("-55"));
        System.out.println(NumberUtils.toInt("666"));
        System.out.println("-55".toString());


    }
}