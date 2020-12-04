package com.daicy.redis.persistence;

import com.daicy.redis.persistence.utils.ByteUtils;
import com.daicy.redis.persistence.utils.CRC64Redis;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.daicy.redis.storage.RedisDb;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.CheckedInputStream;

import static com.daicy.redis.persistence.RdbConstants.*;
import static com.daicy.redis.storage.DictValue.*;
import static java.util.Objects.requireNonNull;

/**
 * @author daichangya
 */
public class RDBInputStream {

    private static final String REDIS_PREAMBLE = new String("REDIS");

    private static final long TO_MILLIS = 1000L;

    private static final int HASH = 0x04;
    private static final int SORTED_SET = 0x03;
    private static final int SET = 0x02;
    private static final int LIST = 0x01;
    private static final int STRING = 0x00;

    private static final int TTL_MILLISECONDS = 252;
    private static final int TTL_SECONDS = 253;
    private static final int SELECT = 254;
    private static final int END_OF_STREAM = 255;

    private static final int REDIS_VERSION = 6;
    private static final int VERSION_LENGTH = 4;
    private static final int REDIS_LENGTH = 5;

    private final CheckedInputStream in;

    public RDBInputStream(InputStream in) {
        this.in = new CheckedInputStream(requireNonNull(in), new CRC64Redis());
    }

    public void parse(List<RedisDb> databases) throws IOException {

        int version = version();

        if (version > REDIS_VERSION) {
            throw new IOException("invalid version: " + version);
        }

        Long expireTime = null;
        RedisDb db = null;
        for (boolean end = false; !end; ) {
            int read = in.read();
            switch (read) {
                case SELECT:
                    db = databases.get(rdbLoadLen().len);
                    break;
                case TTL_SECONDS:
                    expireTime = parseTimeSeconds();
                    break;
                case TTL_MILLISECONDS:
                    expireTime = parseTimeMillis();
                    break;
                case STRING:
                    DictKey key = readKey();
                    ensure(db, key, DictValue.string(readString()), expireTime);
                    expireTime = null;
                    break;
                case LIST:
                    ensure(db, readKey(), readList(), expireTime);
                    expireTime = null;
                    break;
                case SET:
                    ensure(db, readKey(), readSet(), expireTime);
                    expireTime = null;
                    break;
                case SORTED_SET:
                    ensure(db, readKey(), readSortedSet(), expireTime);
                    expireTime = null;
                    break;
                case HASH:
                    ensure(db, readKey(), readHash(), expireTime);
                    expireTime = null;
                    break;
                case END_OF_STREAM:
                    // end of stream
                    end = true;
                    db = null;
                    expireTime = null;
                    break;
                default:
                    throw new IOException("not supported: " + read);
            }
        }

        verifyChecksum();

    }

    private long parseTimeSeconds() throws IOException {
        byte[] seconds = read(Integer.BYTES);
        return Ints.fromByteArray(seconds) * TO_MILLIS;
    }

    private long parseTimeMillis() throws IOException {
        byte[] millis = read(Long.BYTES);
        return Longs.fromByteArray(millis);
    }

    private void verifyChecksum() throws IOException {
        long calculated = in.getChecksum().getValue();

        long readed = parseChecksum();

        if (calculated != readed) {
            throw new IOException(String.format("invalid checksum: %s %s",calculated, readed));
        }
    }

    private long parseChecksum() throws IOException {
        return ByteUtils.readLong(read(Long.BYTES));
    }

    private int version() throws IOException {
        String redis = new String(read(REDIS_LENGTH));
        if (!redis.equals(REDIS_PREAMBLE)) {
            throw new IOException("not valid stream");
        }
        return parseVersion(read(VERSION_LENGTH));
    }

    private int parseVersion(byte[] version) {
        StringBuilder sb = new StringBuilder();
        for (byte b : version) {
            sb.append((char) b);
        }
        return Integer.parseInt(sb.toString());
    }

    private DictValue readList() throws IOException {
        int size = rdbLoadLen().len;
        List<String> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            list.add(readString());
        }
        return list(list);
    }

    private DictValue readSet() throws IOException {
        int size = rdbLoadLen().len;
        Set<String> set = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            set.add(readString());
        }
        return set(set);
    }

    private DictValue readSortedSet() throws IOException {
        int size = rdbLoadLen().len;
        List<Map.Entry<Double, String>> entries = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            String value = readString();
            Double score = readDouble();
            entries.add(score(score, value));
        }
        return zset(entries);
    }

    private DictValue readHash() throws IOException {
        int size = rdbLoadLen().len;
        Map<String, String> entries = new HashMap<>();
        for (int i = 0; i < size; i++) {
            entries.put(readString(), readString());
        }
        return hash(entries);
    }

    private void ensure(RedisDb db, DictKey key, DictValue value, Long expireTime) throws IOException {
        if (db != null) {
            db.getDict().put(key, value);
            if (null != expireTime) {
                db.getExpires().put(key, DictValue.toLong(expireTime));
            }
        } else {
            throw new IOException("no database selected");
        }
    }


    /**
     * @see #rdbLoadLen
     */
    public static class Len {
        public final Integer len;
        public final boolean encoded;

        private Len(Integer len, boolean encoded) {
            this.len = len;
            this.encoded = encoded;
        }
    }

    /**
     * read bytes 1 or 2 or 5
     * <p>
     * 1. |00xxxxxx| remaining 6 bits represent the length
     * <p>
     * 2. |01xxxxxx|xxxxxxxx| the combined 14 bits represent the length
     * <p>
     * 3. |10xxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx| the remaining 6 bits are discarded.Additional 4 bytes represent the length(big endian in version6)
     * <p>
     * 4. |11xxxxxx| the remaining 6 bits are read.and then the next object is encoded in a special format.so we set encoded = true
     * <p>
     *
     * @return tuple(len, encoded)
     * @throws IOException when read timeout
     */
    public Len rdbLoadLen() throws IOException {
        boolean isencoded = false;
        int rawByte = in.read();
        int type = (rawByte & 0xC0) >> 6;
        Integer value;
        if (type == REDIS_RDB_ENCVAL) {
            isencoded = true;
            value = rawByte & 0x3F;
        } else if (type == REDIS_RDB_6BITLEN) {
            value = rawByte & 0x3F;
        } else if (type == REDIS_RDB_14BITLEN) {
            value = ((rawByte & 0x3F) << 8) | in.read();
        } else if (rawByte == REDIS_RDB_32BITLEN) {
            value = Ints.fromByteArray(read(Integer.BYTES));
        } else {
            throw new AssertionError("unexpected len-type:" + type);
        }
        return new Len(value, isencoded);
    }


//    private int readLength() throws IOException {
//        int firstByte = in.read();
//        // the first two bits determine the encoding
//        int flag = (firstByte & 0xc0) >> 6;
//        if (length < 0x40) {
//            // 1 byte: 00XXXXXX
//            return length;
//        } else if (length < 0x80) {
//            // 2 bytes: 01XXXXXX XXXXXXXX
//            int next = in.read();
//            return readLength(length, next);
//        } else {
//            // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
//            return Ints.fromByteArray(read(Integer.BYTES));
//        }
//    }
//
//    private int readLength(int length, int next) {
//        return ((length & 0x3F) << 8) | (next & 0xFF);
//    }

    private String readString() throws IOException {
        Len lenObj = rdbLoadLen();
        int len = lenObj.len;
        boolean isencoded = lenObj.encoded;
        byte[] bytes;
        if (isencoded) {
            bytes = readSpecialStringEncoded(len);
        }else {
            bytes = read(len);
        }
        return new String(bytes);
    }

    private byte[] readSpecialStringEncoded(int type) throws IOException {
        switch (type) {
            case REDIS_RDB_ENC_INT8:
                return ByteUtils.readInt(read(1),true).toString().getBytes();
            case REDIS_RDB_ENC_INT16:
                byte[] bytes = read(2);
                return ByteUtils.readInt(bytes,true).toString().getBytes();
            case REDIS_RDB_ENC_INT32:
                return ByteUtils.readInt(read(4),true).toString().getBytes();
            case REDIS_RDB_ENC_LZF:
//                return readLzfString();
            default:
                throw new IllegalStateException("Unknown special encoding: " + type);
        }
    }

    private DictKey readKey() throws IOException {
        return new DictKey(readString());
    }

    private Double readDouble() throws IOException {
        return Double.parseDouble(readString().toString());
    }

    public byte[] read(int size) throws IOException {
        byte[] array = new byte[size];
        int read = in.read(array);
        if (read != size) {
            throw new IOException("error reading stream");
        }
        return array;
    }
}
