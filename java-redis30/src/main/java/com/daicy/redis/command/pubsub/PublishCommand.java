/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.pubsub;


import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.GlobPattern;
import com.daicy.redis.RedisClientSession;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.RedisDb;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Command("publish")
@ParamLength(2)
public class PublishCommand implements DBCommand {

  private static final String MESSAGE = "message";

  private static final String PMESSAGE = "pmessage";


  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    String channel = request.getParamStr(0);
    String message = request.getParamStr(1);
    return new IntegerRedisMessage(pubsubPublishMessage(channel,message));
  }

  /* Publish a message
   *
   * 将 message 发送到所有订阅频道 channel 的客户端，
   * 以及所有订阅了和 channel 频道匹配的模式的客户端。
   */
  public static int pubsubPublishMessage(String channel,String message) {
    DefaultRedisServerContext redisServerContext = DefaultRedisServerContext.getInstance();
    int receivers = 0;

    /* Send to clients listening for that channel */
    // 取出包含所有订阅频道 channel 的客户端的链表
    // 并将消息发送给它们
    List<String> sessionIds = redisServerContext.getPubsubChannels().get(channel);
    if(CollectionUtils.isNotEmpty(sessionIds)){
      List<RedisMessage> messageList = Lists.newArrayList();
      messageList.add(new BulkRedisMessage(MESSAGE));
      messageList.add(new BulkRedisMessage(channel));
      messageList.add(new BulkRedisMessage(message));
      RedisMessage redisMessage = new MultiBulkRedisMessage(messageList);
      publish(redisServerContext,sessionIds,redisMessage);
    }

    receivers = receivers + sessionIds.size();
    /* Send to clients listening to matching channels */
    // 将消息也发送给那些和频道匹配的模式
    receivers = receivers + patternPublish(channel, message, redisServerContext);

    // 返回计数
    return receivers;
  }


  public static  int publish(DefaultRedisServerContext redisServerContext, List<String> sessionIds, RedisMessage redisMessage) {
    sessionIds.forEach(sessionId -> {
      RedisClientSession redisClientSession = redisServerContext.getClient(sessionId);
      if (null != redisClientSession) {
        redisClientSession.getChannel().writeAndFlush(redisMessage);
      }
    });
    return sessionIds.size();
  }


  public static int patternPublish(String channel, String message, DefaultRedisServerContext redisServerContext) {
    int receivers = 0;

    Set<Map.Entry<String, List<String>>> channelToSessionIds = redisServerContext.getPubsubPatterns().entrySet().stream()
            .filter(entry -> new GlobPattern(entry.getKey()).match(channel)).collect(Collectors.toSet());
    if (CollectionUtils.isNotEmpty(channelToSessionIds)) {
      for (Map.Entry<String, List<String>> channelToSessionId : channelToSessionIds) {
        List<String> clientSessionIds = channelToSessionId.getValue();
        List<RedisMessage> messageList = Lists.newArrayList();
        messageList.add(new BulkRedisMessage(PMESSAGE));
        messageList.add(new BulkRedisMessage(channelToSessionId.getKey()));
        messageList.add(new BulkRedisMessage(channel));
        messageList.add(new BulkRedisMessage(message));
        RedisMessage redisMessage = new MultiBulkRedisMessage(messageList);
        publish(redisServerContext,clientSessionIds,redisMessage);
        receivers = receivers + clientSessionIds.size();
      }
    }
    return receivers;
  }

}
