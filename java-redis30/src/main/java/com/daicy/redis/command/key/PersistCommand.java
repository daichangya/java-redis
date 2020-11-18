package com.daicy.redis.command.key;


import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.protocal.RedisMessage;

import static com.daicy.redis.storage.DictKey.safeKey;
import static com.daicy.redis.protocal.RedisMessageConstants.ONE;
import static com.daicy.redis.protocal.RedisMessageConstants.ZERO;

@Command("persist")
@ParamLength(1)
public class PersistCommand implements DBCommand {

  @Override
  public RedisMessage execute(RedisDb db, Request request) {
    DictKey dictKey = safeKey(request.getParamStr(0));
    DictValue value = db.getExpires().get(dictKey);
    if (value != null) {
      db.getExpires().remove(dictKey);
      return ONE;
    }
    return ZERO;
  }
}
