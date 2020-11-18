package com.daicy.redis.protocal.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
 
import static java.util.Objects.requireNonNull;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal.io
 * @date:11/17/20
 */
public class RedisSourceInputStream  implements RedisSource {

    private final InputStream input;

    public RedisSourceInputStream(InputStream input) {
        this.input = requireNonNull(input);
    }

    @Override
    public int available() {
        try {
            return input.available();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String readLine() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean cr = false;
            while (true) {
                int read = input.read();

                if (read == -1) {
                    // end of stream
                    break;
                }

                if (read == '\r') {
                    cr = true;
                } else if (cr && read == '\n') {
                    break;
                } else {
                    cr = false;

                    baos.write(read);
                }
            }
            return new String(baos.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String readString(int size) {
        try {
            byte[] buffer = new byte[size + 2];
            int red = input.read(buffer);
            if (red > -1) {
                return new String(Arrays.copyOf(buffer, buffer.length - 2));
            }
            return null;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
