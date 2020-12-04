package com.daicy.redis.persistence.utils;

import com.google.common.primitives.Ints;

/**
 * @author daichangya
 */
public class ByteUtils {

    public static byte[] toByteArray(int value,boolean littleEndian) {
        byte[] b = new byte[Integer.BYTES];
        for (int i = 0; i < b.length; ++i) {
            if(littleEndian){
                b[i] = (byte) (value >> (i << 3));
            }else {
                b[i] = (byte) (value >> (Long.BYTES - i - 1 << 3));
            }
        }
        return b;
    }

    public static byte[] toByteArray(long value,boolean littleEndian) {
        byte[] b = new byte[Long.BYTES];
        for (int i = 0; i < b.length; ++i) {
            if(littleEndian){
                b[i] = (byte) (value >> (i << 3));
            }else {
                b[i] = (byte) (value >> (Long.BYTES - i - 1 << 3));
            }
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
//                return b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);
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
