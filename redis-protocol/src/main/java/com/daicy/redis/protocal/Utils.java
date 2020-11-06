package com.daicy.redis.protocal;

import java.util.Arrays;

import static com.daicy.redis.protocal.Constants.*;

public class Utils {
  static boolean numeric (byte b) {
    return (0x2F < b) && ( b < 0x3A);
  }
  
  /**
    Note: `should` must be sorted!
  */
  static void check (byte is, byte[] should) {
    if (0 > Arrays.binarySearch(should, is)) {
      throw new Protocol.ProtocolException("unexpected byte is: "+is+"("+((char)is)+")");
    } 
  }
  static void check (byte is, byte should) {
    if (is != should) {
      throw new Protocol.ProtocolException("byte is: "+is+"("+((char)is)+") should be:"+should+"("+((char)should)+")");
    }
  }

  static final byte[] MINUS1 = { MINUS, 0x31 };
  static byte[] lenbytes (byte[] bs) {
    if (null == bs) {
      return MINUS1;
    }
    return Integer.toString(bs.length).getBytes();
  }
  static byte[] lenbytes (Object[] bs) {
    return Integer.toString(bs.length).getBytes();
  }
}
