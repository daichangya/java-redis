/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.remoting.transport.netty4;


import io.netty.channel.Channel;

import java.util.Collection;

public interface ServerContext<T> {

    void init();

    void start();

    void stop();

    int getClientSize();

    Collection<T> getClients();

    T addClient(T client);

    T getClient(Channel channel);

    void setServer(Server server);

    Server getServer();
}
