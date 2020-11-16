package com.daicy.redis.protocal;

import java.util.Arrays;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis
 * @date:11/10/20
 */
public class ReplyConstants {

    static final byte ASTERISK = (byte) '*';
    static final byte DOLLAR = (byte) '$';
    static final byte PLUS = (byte) '+';
    static final byte MINUS = (byte) '-';
    static final byte COLON = (byte) ':';
    static final byte CR = (byte) '\r';
    static final byte LF = (byte) '\n';

    static final byte[] CRLF = {CR, LF};
    static final byte[] REPLY = {PLUS, MINUS, COLON, DOLLAR, ASTERISK};

    static {
        Arrays.sort(REPLY);
    }

    public static final IntegerReply ZERO = new IntegerReply(0);

    public static final IntegerReply ONE = new IntegerReply(1);

    public static final StatusReply OK = new StatusReply("OK");

    public static final StatusReply PONG = new StatusReply("PONG");

    public static final StatusReply QUIT = new StatusReply("QUIT");

    public static final BulkReply NULL = new BulkReply();

    public static final ErrorReply NO_KEY = new ErrorReply("ERR no such key");


    public static final ErrorReply OUT_RANGE =
            new ErrorReply("ERR index out of range");

    public static final ErrorReply TYPE_ERROR =
            new ErrorReply("WRONGTYPE Operation against a key holding the wrong kind of value");
}
