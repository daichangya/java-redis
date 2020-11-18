package com.daicy.redis.protocal;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/16/20
 */
public class ByteBufferBuilder {
    private static final int INITIAL_CAPACITY = 1024;

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


    private ByteBuffer buffer = ByteBuffer.allocate(INITIAL_CAPACITY);

    public ByteBufferBuilder append(Object obj) {
        if (obj instanceof Integer) {
            append((Integer) obj);
        } else if (obj instanceof String) {
            append((String) obj);
        } else if (obj instanceof byte[]) {
            append((byte[]) obj);
        } else if (obj instanceof Byte) {
            append((Byte) obj);
        } else if (obj instanceof Character) {
            append((Character) obj);
        }
        return this;
    }

    public ByteBufferBuilder append(Integer i) {
        append(String.valueOf(i));
        return this;
    }

    public ByteBufferBuilder append(String str) {
        append(str.getBytes(DEFAULT_CHARSET));
        return this;
    }

    public ByteBufferBuilder append(byte[] buf) {
        ensureCapacity(buf.length);
        buffer.put(buf);
        return this;
    }

    public ByteBufferBuilder append(Byte b) {
        ensureCapacity(1);
        buffer.put(b);
        return this;
    }

    public ByteBufferBuilder append(Character ch) {
        ensureCapacity(1);
        buffer.put((byte) ch.charValue());
        return this;
    }

    public ByteBufferBuilder append(char ch) {
        ensureCapacity(1);
        buffer.put((byte) ch);
        return this;
    }

    private void ensureCapacity(int len) {
        if (buffer.remaining() < len) {
            growBuffer(len);
        }
    }

    private void growBuffer(int len) {
        int capacity = buffer.capacity() + Math.max(len, INITIAL_CAPACITY);
        buffer = ByteBuffer.allocate(capacity).put(build());
    }

    public byte[] build() {
        byte[] array = new byte[buffer.position()];
        buffer.rewind();
        buffer.get(array);
        return array;
    }
}
