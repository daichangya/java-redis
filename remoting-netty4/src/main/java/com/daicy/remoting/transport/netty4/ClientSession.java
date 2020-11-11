package com.daicy.remoting.transport.netty4;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.remoting.transport.netty4
 * @date:11/11/20
 */
public class ClientSession {

    protected Channel channel;

    private String id;

    public ClientSession(String id, Channel channel) {
        this.channel = channel;
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getId() {
        return id;
    }

}
