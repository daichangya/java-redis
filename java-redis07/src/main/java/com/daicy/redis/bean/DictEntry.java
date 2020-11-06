package com.daicy.redis.bean;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.bean
 * @date:11/6/20
 */
public class DictEntry {
//    void *key;
//    void *val;
//    struct dictEntry *next;
    private String key;

    private String val;

    private DictEntry next;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public DictEntry getNext() {
        return next;
    }

    public void setNext(DictEntry next) {
        this.next = next;
    }
}
