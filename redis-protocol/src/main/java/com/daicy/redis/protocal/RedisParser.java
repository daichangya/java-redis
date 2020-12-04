package com.daicy.redis.protocal;

import com.daicy.redis.protocal.io.RedisSource;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.daicy.redis.protocal.RedisMessageConstants.*;
import static java.util.Spliterators.spliteratorUnknownSize;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/17/20
 */
public class RedisParser implements Iterator<RedisMessage> {

    private final int maxLength;
    private final RedisSource source;

    public RedisParser(int maxLength, RedisSource source) {
        this.maxLength = maxLength;
        this.source = Preconditions.checkNotNull(source);
    }

    @Override
    public boolean hasNext() {
        return source.available() > 0;
    }

    @Override
    public RedisMessage next() {
        return parseToken(source.readLine());
    }

    public <T> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    private RedisMessage parseToken(String line) {
        RedisMessage token = new UnknownRedisMessage(null);
        if (line != null && !line.isEmpty()) {
            if (startsWith(line, ARRAY_PREFIX)) {
                int size = Integer.parseInt(line.substring(1));
                token = parseArray(size);
            } else if (startsWith(line, STATUS_PREFIX)) {
                token = new StatusRedisMessage(line.substring(1));
            } else if (startsWith(line, ERROR_PREFIX)) {
                token = new ErrorRedisMessage(line.substring(1));
            } else if (startsWith(line, INTEGER_PREFIX)) {
                token = parseIntegerToken(line.substring(1));
            } else if (startsWith(line, STRING_PREFIX)) {
                token = parseStringToken(line.substring(1));
            } else {
                token = new UnknownRedisMessage(line);
            }
        }
        return token;
    }

    private boolean startsWith(String line, byte bt) {
        return StringUtils.isEmpty(line) ? false : line.charAt(0) == bt;
    }

    private RedisMessage parseIntegerToken(String line) {
        Integer value = Integer.valueOf(line);
        return new IntegerRedisMessage(value);
    }

    private RedisMessage parseStringToken(String line) {
        BulkByteRedisMessage token;
        int length = Integer.parseInt(line);
        if (length > 0 && length < maxLength) {
            token = new BulkByteRedisMessage(source.readByte(length));
        } else {
            token = new BulkByteRedisMessage(null);
        }
        return token;
    }

    private RedisMessage parseArray(int size) {
        List<RedisMessage> array = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            array.add(parseToken(source.readLine()));
        }

        return new MultiBulkRedisMessage(array);
    }
}