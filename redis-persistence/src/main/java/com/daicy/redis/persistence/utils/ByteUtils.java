/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.persistence.utils;

import com.google.common.primitives.Ints;

public class ByteUtils {

    public static byte[] toByteArray(long value) {
        byte[] b = new byte[Long.BYTES];
        for (int i = 0; i < b.length; ++i) {
            b[i] = (byte) (value >> (Long.BYTES - i - 1 << 3));
        }
        return b;
    }


    public static Integer readInt(byte[] bytes) {
        return readInt(bytes, true);
    }

    public static Integer readInt(byte[] bytes, boolean littleEndian) {
        int r = 0;
        int length = bytes.length;
        for (int i = 0; i < length; ++i) {
            final int v = bytes[i] & 0xFF;
            if (littleEndian) {
                r |= (v << (i << 3));
            } else {
                r = (r << 8) | v;
            }
        }
        int c;
        return r << (c = (4 - length << 3)) >> c;
    }

    public static long readLong(byte[] bytes) {
        return readLong(bytes, true);
    }

    public static long readLong(byte[] bytes, boolean littleEndian) {
        long r = 0;
        int length = bytes.length;
        for (int i = 0; i < length; ++i) {
            final long v =  bytes[i] & 0xFF;
            if (littleEndian) {
                r |= (v << (i << 3));
            } else {
                r = (r << 8) | v;
            }
        }
        return r;
    }


    public static void main(String[] args) {
        byte[] bytes = Ints.toByteArray(555);
        System.out.println(bytes);
        System.out.println(readInt(bytes,false));
        System.out.println(readInt(bytes,true));

    }
}
