package com.daicy.remoting.transport.netty4.client;

import com.daicy.remoting.transport.netty4.Server;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.remoting.transport.netty4
 * @date:11/23/20
 */
public interface Client extends Server {

    Channel getChannel();

    void reconnect();

    boolean isStarted();

    ClientBuilder getClientBuilder();

    EventLoopGroup getEventLoopGroup();

}