package com.daicy.redis.original;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.bean
 * @date:11/6/20
 */
//// 计算哈希值的函数
//    unsigned int (*hashFunction)(const void *key);
//
//            // 复制键的函数
//            void *(*keyDup)(void *privdata, const void *key);
//
//            // 复制值的函数
//            void *(*valDup)(void *privdata, const void *obj);
//
//            // 对比键的函数
//            int (*keyCompare)(void *privdata, const void *key1, const void *key2);
//
//            // 销毁键的函数
//            void (*keyDestructor)(void *privdata, void *key);
//
//            // 销毁值的函数
//            void (*valDestructor)(void *privdata, void *obj);
public interface DictType {
    // 计算哈希值的函数
    int hashFunction(String key);

    // 复制键的函数
    String keyDup(Object privdata, String key);

    // 复制值的函数
    Object keyDup(Object privdata, Object obj);

    // 对比键的函数
    String keyCompare(Object privdata, String key1, String key2);

//    // 销毁键的函数
//    String keyDestructor(Object privdata, String key);
//
//    // 销毁值的函数
//    void (*valDestructor)(void *privdata, void *obj);
}
