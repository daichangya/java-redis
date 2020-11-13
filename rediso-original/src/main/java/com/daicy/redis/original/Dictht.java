package com.daicy.redis.original;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.storage
 * @date:11/13/20
 */
public interface Dictht {
//    // 哈希表数组
//    dictEntry **table;
//
//    // 哈希表大小
//    unsigned long size;
//
//    // 哈希表大小掩码，用于计算索引值
//    // 总是等于 size - 1
//    unsigned long sizemask;
//
//    // 该哈希表已有节点的数量
//    unsigned long used;

    DictEntry[] getDictEntry();

    long getSize();

    long getSizeMask();

    long getUsed();
}
