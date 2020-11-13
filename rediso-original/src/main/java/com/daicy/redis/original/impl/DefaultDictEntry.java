package com.daicy.redis.original.impl;


import com.daicy.redis.storage.original.DictEntry;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.storage.impl
 * @date:11/13/20
 */
public class DefaultDictEntry implements DictEntry {
    //    void *key;
//    void *val;
//    struct dictEntry *next;
    private String key;

    private String val;

    private DictEntry next;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getVal() {
        return val;
    }

    @Override
    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public DictEntry getNext() {
        return next;
    }

    @Override
    public void setNext(DictEntry next) {
        this.next = next;
    }
}
