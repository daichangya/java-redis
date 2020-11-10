package com.daicy.redis.utils;

import io.netty.buffer.ByteBuf;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.utils
 * @date:11/10/20
 */
public class ByteBufUtils {
    public static byte[] getBytes(ByteBuf buf) {
        byte[] bytes;
        int offset;
        int length = buf.readableBytes();

        if (buf.hasArray()) {
            bytes = buf.array();
            offset = buf.arrayOffset();
        } else {
            bytes = new byte[length];
            buf.getBytes(buf.readerIndex(), bytes);
            offset = 0;
        }
        return bytes;
    }
}
