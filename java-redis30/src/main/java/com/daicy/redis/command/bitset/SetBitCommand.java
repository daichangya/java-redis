package com.daicy.redis.command.bitset;

import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ParamLength;
import com.daicy.redis.annotation.ParamType;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.ErrorRedisMessage;
import com.daicy.redis.protocal.IntegerRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.daicy.redis.storage.DictKey.safeKey;


@Command("setbit")
@ParamLength(3)
@ParamType(DataType.STRING)
public class SetBitCommand implements DBCommand {

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        try {
            int offset = Integer.parseInt(request.getParamStr(1));
            int bit = Integer.parseInt(request.getParamStr(2));
            AtomicBoolean isHave = new AtomicBoolean(false);
            db.getDict().merge(db, safeKey(request.getParamStr(0)),
                    DictValue.bitset(),
                    (oldValue, newValue) -> {
                        BitSet bitSet = BitSet.valueOf(oldValue.getString().getBytes());
                        isHave.set(bitSet.get(offset));
                        bitSet.set(offset, bit != 0);
                        return oldValue;
                    });
            return new IntegerRedisMessage(isHave.get() ? 1 : 0);
        } catch (NumberFormatException e) {
            return new ErrorRedisMessage("bit or offset is not an integer");
        }
    }
}
