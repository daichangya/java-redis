package com.daicy.redis.protocal;

import ch.qos.logback.core.CoreConstants;
import com.google.common.base.Charsets;

import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: sam
 * Date: 7/29/11
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class BulkReply implements Reply<byte[]> {
    public static final char MARKER = CoreConstants.DOLLAR;
    private byte[] bytes;

    public BulkReply() {
    }

    public BulkReply(byte[] bytes) {
        this.bytes = bytes;
    }

    public BulkReply(String message) {
        this(message.getBytes());
    }

    @Override
    public byte[] data() {
        return bytes;
    }

    public String asAsciiString() {
        if (bytes == null) return null;
        return new String(bytes, Charsets.US_ASCII);
    }

    public String asUTF8String() {
        if (bytes == null) return null;
        return new String(bytes, Charsets.UTF_8);
    }

    public String asString(Charset charset) {
        if (bytes == null) return null;
        return new String(bytes, charset);
    }

}
