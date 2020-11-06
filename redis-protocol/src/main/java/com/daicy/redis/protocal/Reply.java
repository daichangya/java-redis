package com.daicy.redis.protocal;


import java.util.List;
import java.util.LinkedList;
import java.nio.ByteBuffer;

import com.daicy.primitive.collection.ByteList;

import static com.daicy.redis.protocal.Utils.numeric;
import static com.daicy.redis.protocal.Utils.lenbytes;
import static com.daicy.redis.protocal.Constants.*;



public abstract class Reply {

  enum Type {
    MULTI,
    BULK,
    INT,
    STATUS,
    ERROR
  }

  static Reply makeReply(byte indicator) {
    switch (indicator) {
      case ASTERISK:
        return new MultiBulkReply();
      case DOLLAR:
        return new BulkReply();
      case COLON:
        return new IntegerReply();
      case PLUS:
        return new StatusReply();
      case MINUS:
        return new ErrorReply();
      default:
        throw new Protocol.ProtocolException("unexpected: "+(char)indicator);
    }
  }

  Type type;

  /* indicate next arg in req or multibulk*/
  void next(){}

  /* collect the next byte, internal to sm */
  void set(byte b){}

  boolean isBulk() {
    return false;
  }
  boolean isMultibulk() {
    return false;
  }

  static class StringReply extends Reply {
    StringBuffer buf = new StringBuffer();
    
    public void set(byte b) {
      buf.append((char)b);
    }
    public String getMessage() {
      return buf.toString();
    }

    public String toString() {
      return this.getClass()+" : "+this.getMessage();
    }
  }

  /** e.g. +OK */
  public static class StatusReply extends StringReply {
    StatusReply() {
      this.type = Type.STATUS;
    }
  }

  /** e.g. -Some error */
  public static class ErrorReply extends StringReply {
    ErrorReply () {
      this.type = Type.ERROR;
    }
  }

  /** e.g. :1000 */
  public static class IntegerReply extends Reply {
    long value;
    
    IntegerReply() {
      this.type = Type.INT;
    }

    public void set(byte b){
      if (!numeric(b)) {
        throw new Protocol.ProtocolException("not numeric");
      }
      value *= 10;
      value += b - 0x30;
    }

    public long getValue() {
      return value;
    }

    public String toString () {
      return this.getClass()+" : "+this.getValue();
    }
  }

  public static class BulkReply extends Reply {
    ByteList bytes;
    boolean isNull;

    BulkReply() {
      this.type = Type.BULK;
    }

    public void set(byte b) {
      this.bytes = bytes == null ? new ByteList(32) : this.bytes;
      this.bytes.add(b);
    }

    public boolean isBulk() {
      return true;
    }

    public byte[] getValue() {
      if (isNull) return null;
      return this.bytes.toArray();
    }

    public String toString() {
      return isNull ? "null" : new String(this.getValue());
    }
  }

  public static class MultiBulkReply extends BulkReply {
    List<byte[]> byteList;

    MultiBulkReply () {
      this.type = Type.MULTI;
    }

    public void next(){
      this.byteList = null == this.byteList ? new LinkedList<byte[]>() : this.byteList;
      this.byteList.add(super.getValue());
      if (null != this.bytes) this.bytes.clear();
      this.isNull = false;
    }

    public List<byte[]> getEntries() {
      return this.byteList;
    }

    public String toString() {
      if (null == this.byteList) {
        return "";
      }
      StringBuilder buf = new StringBuilder();
      for (byte[] bs : this.byteList){
        if (0 != buf.length()) {
          buf.append(':');
        }
        buf.append(null == bs ? "null" : new String(bs));
        //buf.append(new String(bs));
      }
      return buf.toString();
    }
    public boolean isMultibulk() {
      return true;
    }
  }
  
  static byte[] encode (String status, byte prfx) {
    byte [] s = status.getBytes();
    byte [] b = new byte[s.length + 3];
    b[0] = prfx;
    System.arraycopy(s, 0, b, 1, s.length);
    System.arraycopy(CRLF, 0, b, s.length+1, 2);
    return b;
  }

  static ByteBuffer encode (String status, ByteBuffer buf, byte prfx) {
    if (null == buf) {
      return ByteBuffer.wrap(encode(status, prfx));
    }
    buf.put(prfx);
    for (byte b : status.getBytes()) {
      buf.put(b);
    }
    buf.put(CRLF);
    return buf;
  }

  public static byte [] encodeStatus (String status) {
    return encode(status, PLUS);
  }

  public static ByteBuffer encodeStatus (String status, ByteBuffer buf) {
    return encode(status, buf, PLUS);
  }

  public static byte [] encodeError (String status) {
    return encode(status, MINUS);
  }

  public static ByteBuffer encodeError (String status, ByteBuffer buf) {
    return encode(status, buf, MINUS);
  }
  
  public static byte [] encodeInteger (long i) {
    String s = Long.toString(i);
    return encode(s, COLON);
  }

  public static ByteBuffer encodeInteger (long i, ByteBuffer buf) {
    String s = Long.toString(i);
    return encode(s, buf, COLON);
  }
  
  public static byte [] encodeBulk (byte[] bs) {
    byte [] ll = lenbytes(bs);
    int len = ll.length + bs.length + 3; // $ CRLF 
    if (null != bs) {
      len += 2;  // bs CRLF
    }

    byte [] ret = new byte[len];
    ret[0] = DOLLAR;
    System.arraycopy(ll,   0, ret, 1, ll.length);
    System.arraycopy(CRLF, 0, ret, ll.length+1, 2);
    if (null != bs) {
      System.arraycopy(bs, 0, ret, ll.length + 3, bs.length);
      System.arraycopy(CRLF, 0, ret, ret.length-2, 2);
    }
    return bs;
  }

  public static byte [] encodeBulk (String s) {
    return null == s ? encodeBulk((byte[])null) : encodeBulk(s.getBytes());
  }

  public static ByteBuffer encodeBulk (byte[] bs, ByteBuffer buf) {
    if (null == buf) {
      return ByteBuffer.wrap(encodeBulk(bs));
    }
    buf.put(DOLLAR);
    buf.put(lenbytes(bs));
    buf.put(CRLF);
    if (null != bs) {
      buf.put(bs);
      buf.put(CRLF);
    }
    return buf;
  }

  public static ByteBuffer encodeBulk(String s, ByteBuffer buf) {
    return null == s ? encodeBulk((byte[])null, buf) : encodeBulk(s.getBytes(), buf);
  }

  static final byte[][]TYPE = new byte[0][];

  public static byte[] encodeMultibulk(List<byte[]> bs) {
    byte[][] bbs = bs.toArray(TYPE);
    return encodeMultibulk(bbs);
  }
  public static byte[] encodeMultibulkString(List<String> ss) {
    List<byte[]> list = new LinkedList<byte[]>();
    for (String s:ss) {
      list.add(s.getBytes());
    }
    return encodeMultibulk(list);
  }

  public static byte[] encodeMultibulk(byte[][] bbs) {
    ByteList list = new ByteList(bbs.length * 10);
    list.add(ASTERISK);
    list.add(lenbytes(bbs));
    list.add(CRLF);
    for (byte [] bs : bbs) {
      list.add(DOLLAR);
      list.add(lenbytes(bs));
      list.add(CRLF);
      list.add(bs);
      list.add(CRLF);
    }
    return list.toArray();
  }

  public static ByteBuffer encodeMultibulk(byte [][] bbs, ByteBuffer buf) {
    if (null == buf) {
      return ByteBuffer.wrap(encodeMultibulk(bbs));
    }
    buf.put(DOLLAR);
    buf.put(lenbytes(bbs));
    buf.put(CRLF);
    for (byte [] bs : bbs) {
      encodeBulk(bs, buf);
    }
    return buf;
  }

  public static ByteBuffer encodeMultibulk(List<byte[]> bs, ByteBuffer buf) {
    byte[][] bbs = bs.toArray(TYPE);
    return encodeMultibulk(bbs, buf);

  }
  
  public static ByteBuffer encodeMultibulkString(List<String> ss, ByteBuffer buf) {
    List<byte[]> list = new LinkedList<byte[]>();
    for (String s : ss) {
      list.add(s.getBytes());
    }
    return encodeMultibulk(list, buf);
  }
 
}
