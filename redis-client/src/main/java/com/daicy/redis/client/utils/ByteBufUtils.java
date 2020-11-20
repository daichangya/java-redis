package com.daicy.redis.client.utils;

import com.daicy.redis.client.codec.StringCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.utils
 * @date:11/10/20
 */
public class ByteBufUtils {

    private static StringCodec stringCodec = new StringCodec();

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

    public static ByteBuf toByteBuf(byte[] bytes) {
        return Unpooled.wrappedBuffer(bytes);
    }

    public static ByteBuf toByteBuf(String message) {
        return Unpooled.wrappedBuffer(stringCodec.encodeValue(message));
    }
}
