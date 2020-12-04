package com.daicy.redis.persistence;


import com.daicy.redis.context.DBConfig;
import com.daicy.redis.persistence.utils.ByteUtils;
import com.daicy.redis.persistence.utils.CRC64Redis;
import com.daicy.redis.persistence.utils.NumberUtils;
import com.daicy.redis.storage.DataType;
import com.daicy.redis.storage.DictKey;
import com.daicy.redis.storage.DictValue;
import com.google.common.primitives.Ints;
import com.ning.compress.lzf.LZFEncoder;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.CheckedOutputStream;

import static com.daicy.redis.persistence.RdbConstants.*;
import static java.util.Objects.requireNonNull;

/**
 * https://github.com/leonchen83/redis-replicator/wiki/RDB-dump-data-format
 * @author daichangya
 */
public class RDBOutputStream {

    private static final byte[] REDIS = "REDIS".getBytes();

    private static final int TTL_MILLISECONDS = 0xFC;
    private static final int END_OF_STREAM = 0xFF;
    private static final int SELECT = 0xFE;

    private final CheckedOutputStream out;

    public RDBOutputStream(OutputStream out) {
        this.out = new CheckedOutputStream(requireNonNull(out), new CRC64Redis());
    }

    public void preamble(int version) throws IOException {
        out.write(REDIS);
        out.write(version(version));
    }

    private byte[] version(int version) {
        StringBuilder sb = new StringBuilder(String.valueOf(version));
        for (int i = sb.length(); i < Integer.BYTES; i++) {
            sb.insert(0, '0');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void select(int db) throws IOException {
        out.write(SELECT);
        rdbSaveLen(db);
    }

    public void dabatase(Map<DictKey, DictValue> dict, Map<DictKey, DictValue> expires) throws IOException {
        for (Entry<DictKey, DictValue> entry : dict.entrySet()) {
            value(entry.getKey(), entry.getValue(), expires.get(entry.getKey()));
        }
    }

    private void value(DictKey key, DictValue value, DictValue expired) throws IOException {
        if (null != expired) {
            boolean isExpired = expired.isExpired(Instant.now());
            if (isExpired) {
                return;
            }
            expiredAt(expired);
        }
        type(value.getType());
        key(key);
        value(value);
    }

    private void expiredAt(DictValue expired) throws IOException {
        if (expired != null) {
            out.write(TTL_MILLISECONDS);
            out.write(ByteUtils.toByteArray(expired.getExpiredAt().toEpochMilli(),true));
        }
    }

    private void type(DataType type) throws IOException {
        out.write(type.getValue());
    }

    private void key(DictKey key) throws IOException {
        string(key.getValue());
    }

    private void value(DictValue value) throws IOException {
        switch (value.getType()) {
            case STRING:
                string(value.getString());
                break;
            case LIST:
                list(value.getList());
                break;
            case HASH:
                hash(value.getHash());
                break;
            case SET:
                set(value.getSet());
                break;
            case ZSET:
                zset(value.getSortedSet());
                break;
            default:
                break;
        }
    }


    /* Saves an encoded rdbSaveLen. The first two bits in the first byte are used to
     * hold the encoding type. See the REDIS_RDB_* definitions for more information
     * on the types of encoding.
     *
     * 对 len 进行特殊编码之后写入到 rdb 。
     *
     * 写入成功返回保存编码后的 len 所需的字节数。
     */
    private int rdbSaveLen(int len) throws IOException {
        byte[] buf = new byte[2];
        int nwritten;

        if (len < (1 << 6)) {
            /* Save a 6 bit len */
            buf[0] = (byte) ((len & 0xFF) | (REDIS_RDB_6BITLEN << 6));
            out.write(buf[0]);
            nwritten = 1;

        } else if (len < (1 << 14)) {
            /* Save a 14 bit len */
            buf[0] = (byte) (((len >> 8) & 0xFF) | (REDIS_RDB_14BITLEN << 6));
            buf[1] = (byte) (len & 0xFF);
            out.write(buf);
            nwritten = 2;

        } else {
            /* Save a 32 bit len */
            buf[0] = (byte) (REDIS_RDB_32BITLEN << 6);
            out.write(buf[0]);
            out.write(Ints.toByteArray(len));
            nwritten = 1 + 4;
        }
        return nwritten;
    }

//    private void length(int length) throws IOException {
//        if (length < 0x40) {
//            // 1 byte: 00XXXXXX
//            out.write(length);
//        } else if (length < 0x4000) {
//            // 2 bytes: 01XXXXXX XXXXXXXX
//            int b1 = length >> 8;
//            int b2 = length & 0xFF;
//            out.write(0x40 | b1);
//            out.write(b2);
//        } else {
//            // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
//            out.write(0x80);
//            out.write(ByteUtils.toByteArray(length));
//        }
//    }


    /* Encodes the "value" argument as integer when it fits in the supported ranges
     * for encoded types. If the function successfully encodes the integer, the
     * representation is stored in the buffer pointer to by "enc" and the string
     * length is returned. Otherwise 0 is returned.
     *
     * 尝试使用特殊的整数编码来保存 value ，这要求它的值必须在给定范围之内。
     *
     * 如果可以编码的话，将编码后的值保存在 enc 指针中，
     * 并返回值在编码后所需的长度。
     *
     * 如果不能编码的话，返回 0 。
     */
    private int rdbEncodeInteger(int value) throws IOException {
        if (value >= -(1 << 7) && value <= (1 << 7) - 1) {
            byte[] enc = new byte[2];
            enc[0] = (byte) ((REDIS_RDB_ENCVAL << 6) | REDIS_RDB_ENC_INT8);
            enc[1] = (byte) (value & 0xFF);
            out.write(enc);
            return 2;

        } else if (value >= -(1 << 15) && value <= (1 << 15) - 1) {
            byte[] enc = new byte[3];
            enc[0] = (byte) ((REDIS_RDB_ENCVAL << 6) | REDIS_RDB_ENC_INT16);
            enc[1] = (byte) (value & 0xFF);
            enc[2] = (byte) ((value >> 8) & 0xFF);
            out.write(enc);
            return 3;

        } else if (value >= -(1 << 31) && value <= (1 << 31) - 1) {
            byte[] enc = new byte[5];
            enc[0] = (byte) ((REDIS_RDB_ENCVAL << 6) | REDIS_RDB_ENC_INT32);
            enc[1] = (byte) (value & 0xFF);
            enc[2] = (byte) ((value >> 8) & 0xFF);
            enc[3] = (byte) ((value >> 16) & 0xFF);
            enc[4] = (byte) ((value >> 24) & 0xFF);
            out.write(enc);
            return 5;

        } else {
            return 0;
        }
    }

    private void string(String value) throws IOException {
        byte[] bytes = value.getBytes();
        int length = bytes.length;
        if (length <= 11) {
            Integer intValue = NumberUtils.toInt(value, null);
            if (null != intValue && StringUtils.equals(value, intValue.toString())) {
                rdbEncodeInteger(intValue);
                return;
            }
        }

        /* Try LZF compression - under 20 bytes it's unable to compress even
         * aaaaaaaaaaaaaaaaaa so skip it
         *
         * 如果字符串长度大于 20 ，并且服务器开启了 LZF 压缩，
         * 那么在保存字符串到数据库之前，先对字符串进行 LZF 压缩。
         */
        if (DBConfig.rdb_compression && length > 20) {

            rdbSaveLzfStringObject(bytes);
            return;
            /* Return value of 0 means data can't be compressed, save the old way */
        }

        rdbSaveLen(length);
        out.write(bytes);
    }


    /*
     * 尝试对输入字符串 s 进行压缩，
     * 如果压缩成功，那么将压缩后的字符串保存到 rdb 中。
     *
     * 函数在成功时返回保存压缩后的 s 所需的字节数，
     * 压缩失败或者内存不足时返回 0 ，
     * 写入失败时返回 -1 。
     */
   private void rdbSaveLzfStringObject(byte[] bytes) throws IOException {
       int length = bytes.length;
       byte[] outbytes = LZFEncoder.encode(bytes);

        /* Data compressed! Let's save it on disk
         *
         * 保存压缩后的字符串到 rdb 。
         */

        // 写入类型，说明这是一个 LZF 压缩字符串
        int type = (REDIS_RDB_ENCVAL<<6)|REDIS_RDB_ENC_LZF;
        out.write(type);
        rdbSaveLen(outbytes.length);
        rdbSaveLen(length);
        out.write(outbytes);
    }


    private void string(double value) throws IOException {
        string(String.valueOf(value));
    }

    private void list(List<String> value) throws IOException {
        rdbSaveLen(value.size());
        for (String item : value) {
            string(item);
        }
    }

    private void hash(Map<String, String> value) throws IOException {
        rdbSaveLen(value.size());
        for (Entry<String, String> entry : value.entrySet()) {
            string(entry.getKey());
            string(entry.getValue());
        }
    }

    private void set(Set<String> value) throws IOException {
        rdbSaveLen(value.size());
        for (String item : value) {
            string(item);
        }
    }

    private void zset(Set<Entry<Double, String>> value) throws IOException {
        rdbSaveLen(value.size());
        for (Entry<Double, String> item : value) {
            string(item.getValue());
            string(item.getKey());
        }
    }

    public void end() throws IOException {
        out.write(END_OF_STREAM);
        out.write(ByteUtils.toByteArray(out.getChecksum().getValue(),true));
        out.flush();
    }
}
