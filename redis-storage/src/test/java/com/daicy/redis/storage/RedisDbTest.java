package com.daicy.redis.storage;

import com.daicy.collections.CowArrayList;
import com.daicy.collections.CowList;
import org.junit.Test;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.storage
 * @date:11/25/20
 */
public class RedisDbTest {

    @Test
    public void testArrayList(){
        Integer a = 0;
        int b = 0;
        for (int i = 0; i < 1; i++) {
//            a = a ++;
            b = ++ a;
            System.out.println(a);
            System.out.println(b);
        }
        CowArrayList<String> beatles = new CowArrayList<>();

        beatles.add( "john" );
        beatles.add( "paul" );
        beatles.add( "george" );
        beatles.add( "ringo" );

        for (int i = 0; i < 100; i++) {
            beatles.add(""+i);
        }

        CowList<String> famous = beatles.fork();

        beatles.add( "pete" );
        beatles.set(10,"8888");
        famous.add( "peter" );
        famous.add( "paul" );
        famous.add( "mary" );
        System.out.println("famous: " + famous);
        System.out.println("beatles: " + beatles);
    }


}