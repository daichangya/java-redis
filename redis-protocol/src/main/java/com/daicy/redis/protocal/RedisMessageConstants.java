package com.daicy.redis.protocal;

import java.util.Arrays;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/16/20
 */
public class RedisMessageConstants {

    static final byte ARRAY_PREFIX = (byte) '*';
    static final byte STRING_PREFIX = (byte) '$';
    static final byte STATUS_PREFIX = (byte) '+';
    static final byte ERROR_PREFIX = (byte) '-';
    static final byte INTEGER_PREFIX = (byte) ':';
    static final byte CR = (byte) '\r';
    static final byte LF = (byte) '\n';

    static final byte[] CRLF = {CR, LF};
    static final byte[] MESSAGE = {STATUS_PREFIX, ERROR_PREFIX, INTEGER_PREFIX, STRING_PREFIX, ARRAY_PREFIX};

    static {
        Arrays.sort(MESSAGE);
    }

    public static final IntegerRedisMessage ZERO = new IntegerRedisMessage(0);

    public static final IntegerRedisMessage ONE = new IntegerRedisMessage(1);

    public static final StatusRedisMessage OK = new StatusRedisMessage("OK");

    public static final StatusRedisMessage PONG = new StatusRedisMessage("PONG");

    public static final StatusRedisMessage QUIT = new StatusRedisMessage("QUIT");

    public static final BulkRedisMessage NULL = new BulkRedisMessage(null);

    public static final ErrorRedisMessage ERR = new ErrorRedisMessage("ERR");

    public static final ErrorRedisMessage NO_KEY = new ErrorRedisMessage("ERR no such key");


    public static final ErrorRedisMessage OUT_RANGE =
            new ErrorRedisMessage("ERR index out of range");

    public static final ErrorRedisMessage TYPE_ERROR =
            new ErrorRedisMessage("WRONGTYPE Operation against a key holding the wrong kind of value");
}
