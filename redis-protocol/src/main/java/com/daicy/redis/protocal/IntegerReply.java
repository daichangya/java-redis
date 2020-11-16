package com.daicy.redis.protocal;


/**
 * Created by IntelliJ IDEA.
 * User: sam
 * Date: 7/29/11
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class IntegerReply implements Reply<Long> {
    public static final char MARKER = ReplyConstants.COLON;
    private final long integer;

    public IntegerReply(long integer) {
        this.integer = integer;
    }

    @Override
    public Long data() {
        return integer;
    }

}
