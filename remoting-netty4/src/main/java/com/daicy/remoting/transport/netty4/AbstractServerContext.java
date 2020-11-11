package com.daicy.remoting.transport.netty4;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.remoting.transport.netty4
 * @date:11/11/20
 */
public abstract class AbstractServerContext implements ServerContext<ClientSession> {
    private final ConcurrentHashMap<String, ClientSession> clients = new ConcurrentHashMap<>();

    @Override
    public int getClientSize() {
        return clients.size();
    }

    @Override
    public Collection getClients() {
        return clients.values();
    }

    @Override
    public ClientSession addClient(ClientSession client) {
        return clients.putIfAbsent(client.getId(), client);
    }

    @Override
    public ClientSession getClient(Channel channel) {
        return clients.computeIfAbsent(sourceKey(channel),key -> newSession(channel));
    }

    protected ClientSession newSession(Channel channel) {
        return new ClientSession(sourceKey(channel),channel);
    }

    public String sourceKey(Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        return remoteAddress.getHostName() + ":" + remoteAddress.getPort();
    }
}
