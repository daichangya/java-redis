package com.daicy.redis.command.key;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.protocal.Reply;

import static com.daicy.redis.storage.DictKey.safeKey;
import static com.daicy.redis.protocal.ReplyConstants.ONE;
import static com.daicy.redis.protocal.ReplyConstants.ZERO;

@Command("persist")
@ParamLength(1)
public class PersistCommand implements DBCommand {

  @Override
  public Reply execute(RedisDb db, Request request) {
    DictKey dictKey = safeKey(request.getParamStr(0));
    DictValue value = db.getExpires().get(dictKey);
    if (value != null) {
      db.getExpires().remove(dictKey);
      return ONE;
    }
    return ZERO;
  }
}
