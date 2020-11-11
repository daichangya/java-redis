/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.daicy.remoting.transport.netty4;


import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.CompletionStage;


/**
 * Remoting Server.
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Client%E2%80%93server_model">Client/Server</a>
 */
public interface Server {

    /**
     * Bind and start the server.
     *
     * @return {@code this} object
     * @throws IllegalStateException if already started
     * @throws IOException           if unable to bind
     * @since 1.0.0
     */
    public abstract CompletionStage<Server> start() throws IOException;

    /**
     * Returns the port number the server is listening on.  This can return -1 if there is no actual
     * port or the result otherwise does not make sense.  Result is undefined after the server is
     * terminated.  If there are multiple possible ports, this will return one arbitrarily.
     * Implementations are encouraged to return the same port on each call.
     *
     * @throws IllegalStateException if the server has not yet been started.
     * @since 1.0.0
     */
    public int getPort();


    /**
     * Initiates an orderly shutdown in which preexisting calls continue but new calls are rejected.
     *
     * @return {@code this} object
     * @since 1.0.0
     */
    public abstract CompletionStage<Server> shutdown();


    /**
     * Returns whether the server is shutdown. Shutdown servers reject any new calls, but may still
     * have some calls being processed.
     *
     * @see #shutdown()
     * @since 1.0.0
     */
    public abstract boolean isShutdown();


    public SocketAddress getLocalAddress();


}
