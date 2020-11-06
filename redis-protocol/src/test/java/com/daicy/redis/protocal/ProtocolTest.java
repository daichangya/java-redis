package com.daicy.redis.protocal;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/6/20
 */
public class ProtocolTest {

    @Test
    public void handleBytes() {
        byte [] bs = "*3\r\n$3\r\nSET\r\n$5\r\nmykey\r\n$7\r\nmyvalue\r\n*3\r\n$3\r\nSET\r\n$5\r\nmykey\r\n$7\r\nmyvalue\r\n".getBytes();
        byte [] i  = ":1000\r\n".getBytes();
        byte [] ok  = "+OK\r\n".getBytes();
        byte [] nok  = "-NOK\r\n".getBytes();
        byte [] blk = "$4\r\nTEST\r\n".getBytes();
        byte [] nul = "$-1\r\n".getBytes();
        byte [] mbn = "*3\r\n$-1\r\n$3\r\nONE\r\n$3\r\nTWO\r\n".getBytes();

        Protocol.CB cb = new Protocol.CB() {
            public void cb (Reply r) {
                System.out.println(r);
            }
        };
        Protocol p = new Protocol(cb);
        p.handleBytes(bs);
        p.handleBytes(i);
        p.handleBytes(ok);
        p.handleBytes(nok);
        p.handleBytes(blk);
        p.handleBytes(nul);
        p.handleBytes(mbn);
    }
}