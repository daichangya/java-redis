package com.daicy.redis.original;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.bean
 * @date:11/6/20
 */
public class Dict {
//// 类型特定函数
//    dictType *type;
//
//    // 私有数据
//    void *privdata;
//
//    // 哈希表
//    dictht ht[2];
//
//    // rehash 索引
//    // 当 rehash 不在进行时，值为 -1
//    int rehashidx; /* rehashing not in progress if rehashidx == -1 */
//
//    // 目前正在运行的安全迭代器的数量
//    int iterators; /* number of iterators currently running */

    private DictType type;

    private Dictht[] ht;

    private Object privdata;

    private int rehashidx;

    private int iterators;

    public DictType getType() {
        return type;
    }

    public void setType(DictType type) {
        this.type = type;
    }

    public Dictht[] getHt() {
        return ht;
    }

    public void setHt(Dictht[] ht) {
        this.ht = ht;
    }

    public Object getPrivdata() {
        return privdata;
    }

    public void setPrivdata(Object privdata) {
        this.privdata = privdata;
    }

    public int getRehashidx() {
        return rehashidx;
    }

    public void setRehashidx(int rehashidx) {
        this.rehashidx = rehashidx;
    }

    public int getIterators() {
        return iterators;
    }

    public void setIterators(int iterators) {
        this.iterators = iterators;
    }
}
