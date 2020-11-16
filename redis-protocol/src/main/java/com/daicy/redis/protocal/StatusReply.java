package com.daicy.redis.protocal;

import java.nio.charset.Charset;

/**
* Created by IntelliJ IDEA.
* User: sam
* Date: 7/29/11
* Time: 10:22 AM
* To change this template use File | Settings | File Templates.
*/
public class StatusReply implements Reply<String> {
  public static final char MARKER = ReplyConstants.PLUS;
  private final String status;
  private byte[] statusBytes;

  public StatusReply(String status) {
    this.status = status;
    this.statusBytes = status.getBytes();
  }

  public StatusReply(byte[] statusBytes, Charset charset) {
    this.status = new String(statusBytes, charset);
    this.statusBytes = statusBytes;
  }

  @Override
  public String data() {
    return status;
  }

}
