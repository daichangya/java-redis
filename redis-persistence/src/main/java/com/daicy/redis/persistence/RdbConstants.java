package com.daicy.redis.persistence;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.persistence
 * @date:12/4/20
 */
public class RdbConstants {

    /* Objects encoding. Some kind of objects like Strings and Hashes can be
     * internally represented in multiple ways. The 'encoding' field of the object
     * is set to one of this fields for this object. */
    // 对象编码
    public static final int REDIS_ENCODING_RAW = 0;     /* Raw representation */
    public static final int REDIS_ENCODING_INT = 1;     /* Encoded as integer */
    public static final int REDIS_ENCODING_HT = 2;    /* Encoded as hash table */
    public static final int REDIS_ENCODING_ZIPMAP = 3;  /* Encoded as zipmap */
    public static final int REDIS_ENCODING_LINKEDLIST = 4; /* Encoded as regular linked list */
    public static final int REDIS_ENCODING_ZIPLIST = 5; /* Encoded as ziplist */
    public static final int REDIS_ENCODING_INTSET = 6; /* Encoded as intset */
    public static final int REDIS_ENCODING_SKIPLIST = 7;  /* Encoded as skiplist */
    public static final int REDIS_ENCODING_EMBSTR = 8;  /* Embedded sds string encoding */

    /* Defines related to the dump file format. To store 32 bits lengths for short
     * keys requires a lot of space, so we check the most significant 2 bits of
     * the first byte to interpreter the length:
     *
     * 通过读取第一字节的最高 2 位来判断长度
     *
     * 00|000000 => if the two MSB are 00 the len is the 6 bits of this byte
     *              长度编码在这一字节的其余 6 位中
     *
     * 01|000000 00000000 =>  01, the len is 14 byes, 6 bits + 8 bits of next byte
     *                        长度为 14 位，当前字节 6 位，加上下个字节 8 位
     *
     * 10|000000 [32 bit integer] => if it's 01, a full 32 bit len will follow
     *                               长度由后跟的 32 位保存
     *
     * 11|000000 this means: specially encoded object will follow. The six bits
     *           number specify the kind of object that follows.
     *           See the REDIS_RDB_ENC_* defines.
     *           后跟一个特殊编码的对象。字节中的 6 位指定对象的类型。
     *           查看 REDIS_RDB_ENC_* 定义获得更多消息
     *
     * Lenghts up to 63 are stored using a single byte, most DB keys, and may
     * values, will fit inside.
     *
     * 一个字节（的其中 6 个字节）可以保存的最大长度是 63 （包括在内），
     * 对于大多数键和值来说，都已经足够了。
     */
    public static final int REDIS_RDB_6BITLEN = 0;
    public static final int REDIS_RDB_14BITLEN = 1;
    public static final int REDIS_RDB_32BITLEN = 2;
    public static final int REDIS_RDB_ENCVAL = 3;
//            public static final int REDIS_RDB_LENERR UINT_MAX


    /* When a length of a string object stored on disk has the first two bits
     * set, the remaining two bits specify a special encoding for the object
     * accordingly to the following defines:
     *
     * 当对象是一个字符串对象时，
     * 最高两个位之后的两个位（第 3 个位和第 4 个位）指定了对象的特殊编码
     */
    public static final int REDIS_RDB_ENC_INT8 = 0;      /* 8 bit signed integer */
    public static final int REDIS_RDB_ENC_INT16 = 1;     /* 16 bit signed integer */
    public static final int REDIS_RDB_ENC_INT32 = 2;     /* 32 bit signed integer */
    public static final int REDIS_RDB_ENC_LZF = 3;       /* string compressed with FASTLZ */
}
