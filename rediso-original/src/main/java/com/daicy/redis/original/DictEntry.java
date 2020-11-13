package com.daicy.redis.original;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.bean
 * @date:11/6/20
 */
public interface DictEntry {

    String getKey();

    void setKey(String key);

    String getVal();

    void setVal(String val);

    DictEntry getNext();

    void setNext(DictEntry next);
}
