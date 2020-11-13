/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.daicy.redis.codec;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;


/**
 * A {@link ComposedRedisCodec} combines two {@link RedisCodec cdecs} to encode/decode key and value to the command output.
 *
 * @author Dimitris Mandalidis
 * @since 5.2
 */
class ComposedRedisCodec<K, V> implements RedisCodec<K, V> {

    private final RedisCodec<K, ?> keyCodec;
    private final RedisCodec<?, V> valueCodec;

    ComposedRedisCodec(RedisCodec<K, ?> keyCodec, RedisCodec<?, V> valueCodec) {
        Preconditions.checkNotNull(keyCodec, "Key codec must not be null");
        Preconditions.checkNotNull(valueCodec, "Value codec must not be null");
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
    }

    @Override
    public K decodeKey(ByteBuf bytes) {
        return keyCodec.decodeKey(bytes);
    }

    @Override
    public V decodeValue(ByteBuf bytes) {
        return valueCodec.decodeValue(bytes);
    }

    @Override
    public ByteBuf encodeKey(K key) {
        return keyCodec.encodeKey(key);
    }

    @Override
    public ByteBuf encodeValue(V value) {
        return valueCodec.encodeValue(value);
    }
}
