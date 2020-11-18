package com.daicy.redis.protocal;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/16/20
 */
public class StatusRedisMessage extends AbstractRedisMessage<String> {

  public StatusRedisMessage(String status) {
    super(status,RedisMessageType.STATUS);
  }

  @Override
  public byte getMarker() {
    return RedisMessageConstants.STATUS_PREFIX;
  }
}
