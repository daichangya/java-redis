package com.daicy.redis.protocal;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.protocal
 * @date:11/6/20
 */
public class ReplyTest {

    @Test
    public void makeReply() {
        byte [] ok  = "+OK\r\n".getBytes();

        Protocol.CB cb = new Protocol.CB() {
            public void cb (Reply r) {
                System.out.println(r);
            }
        };
        Protocol p = new Protocol(cb);
        p.handleBytes(ok);
    }
}