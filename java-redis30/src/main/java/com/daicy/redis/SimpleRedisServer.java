//package com.daicy.redis;
//
//import com.daicy.redis.common.BytesKeyObjectMap;
//import io.netty.buffer.Unpooled;
//import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
//import io.netty.handler.codec.redis.SimpleStringRedisMessage;
//
//
//public class SimpleRedisServer implements RedisServer {
//
//
//    private BytesKeyObjectMap<Object> data = new BytesKeyObjectMap<Object>();
//    private BytesKeyObjectMap<Long> expires = new BytesKeyObjectMap<Long>();
//    private static int[] mask = {128, 64, 32, 16, 8, 4, 2, 1};
//
//
//    /**
//     * Set the string value of a key
//     * String
//     *
//     * @param key0
//     * @param value1
//     * @return StatusReply
//     */
//    @Override
//    public SimpleStringRedisMessage set(byte[] key0, byte[] value1) throws RedisException {
//        _put(key0, value1);
//        return RedisConstants.OK;
//    }
//
//
//    /**
//     * Get the value of a key
//     * String
//     *
//     * @param key0
//     * @return BulkReply
//     */
//    @Override
//    public FullBulkStringRedisMessage get(byte[] key0) throws RedisException {
//        Object o = _get(key0);
//        if (o instanceof byte[]) {
//            return new FullBulkStringRedisMessage(Unpooled.wrappedBuffer((byte[]) o));
//        }
//        if (o == null) {
//            return FullBulkStringRedisMessage.NULL_INSTANCE;
//        } else {
//            throw invalidValue();
//        }
//    }
//
//    private static RedisException invalidValue() {
//        return new RedisException("Operation against a key holding the wrong kind of value");
//    }
//
//
//    private Object _get(byte[] key0) {
//        Object o = data.get(key0);
//        if (o != null) {
//            Long l = expires.get(key0);
//            if (l != null) {
//                if (l < now()) {
//                    data.remove(key0);
//                    return null;
//                }
//            }
//        }
//        return o;
//    }
//
//    private Object _put(byte[] key, Object value) {
//        expires.remove(key);
//        return data.put(key, value);
//    }
//
//    private Object _put(byte[] key, byte[] value, long expiration) {
//        expires.put(key, expiration);
//        return data.put(key, value);
//    }
//
//    private long now() {
//        return System.currentTimeMillis();
//    }
//
//}
