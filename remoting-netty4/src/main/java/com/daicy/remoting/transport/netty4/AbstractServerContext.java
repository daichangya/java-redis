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
public abstract class AbstractServerContext<T extends ClientSession> implements ServerContext<T> {
    private final ConcurrentHashMap<String, T> clients = new ConcurrentHashMap<>();

    private Server server;

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public int getClientSize() {
        return clients.size();
    }

    @Override
    public Collection getClients() {
        return clients.values();
    }

    @Override
    public T getClient(String sessionId) {
        return clients.get(sessionId);
    }

    @Override
    public T addClient(T client) {
        return clients.putIfAbsent(client.getId(), client);
    }


    @Override
    public boolean delClient(ClientSession client) {
        return clients.remove(sourceKey(client.channel),client);
    }


    @Override
    public T getClient(Channel channel) {
        return clients.computeIfAbsent(sourceKey(channel), key -> newSession(channel));
    }

    public T newSession(Channel channel) {
        return (T) new ClientSession(sourceKey(channel), channel);
    }

    public String sourceKey(Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        return remoteAddress.getHostName() + ":" + remoteAddress.getPort();
    }
}
