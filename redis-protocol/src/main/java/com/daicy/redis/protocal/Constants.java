package com.daicy.redis.protocal;

import java.util.Arrays;

public class Constants {
  static final byte ASTERISK = (byte)'*';
  static final byte DOLLAR   = (byte)'$';
  static final byte PLUS     = (byte)'+';
  static final byte MINUS    = (byte)'-';
  static final byte COLON    = (byte)':';
  static final byte CR       = (byte)'\r';
  static final byte LF       = (byte)'\n';

  static final byte [] CRLF  = {CR, LF};
  static final byte [] REPLY = {PLUS, MINUS, COLON, DOLLAR, ASTERISK};
  static {
    Arrays.sort(REPLY);
  }



}
