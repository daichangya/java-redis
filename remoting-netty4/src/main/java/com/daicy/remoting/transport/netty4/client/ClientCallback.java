/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.remoting.transport.netty4.client;


import io.netty.channel.socket.SocketChannel;

public interface ClientCallback<T> {
  void channel(SocketChannel channel);
  void onConnect();
  void onDisconnect();
}
