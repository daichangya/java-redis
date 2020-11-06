package com.daicy.redis.bean;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.bean
 * @date:11/6/20
 */
public class Dict {
//    dictEntry **table;
//    dictType *type;
//    unsigned int size;
//    unsigned int sizemask;
//    unsigned int used;
//    void *privdata;
    private DictEntry[] table;

    private DictType type;

    private int size;

    private int sizemask;

    private int used;

    private Object privdata;

    public DictEntry[] getTable() {
        return table;
    }

    public void setTable(DictEntry[] table) {
        this.table = table;
    }

    public DictType getType() {
        return type;
    }

    public void setType(DictType type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSizemask() {
        return sizemask;
    }

    public void setSizemask(int sizemask) {
        this.sizemask = sizemask;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public Object getPrivdata() {
        return privdata;
    }

    public void setPrivdata(Object privdata) {
        this.privdata = privdata;
    }
}
