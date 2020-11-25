/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.daicy.remoting.transport.netty4.httpserver;

import com.daicy.remoting.transport.netty4.Server;
import com.daicy.remoting.transport.netty4.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 */
@Slf4j
public final class HttpServer {

    static final int PORT = 8080;


    public static void main(String[] args) throws Exception {
        Server httpServer = ServerBuilder.forPort(PORT).channelInitializer(new HttpServerInitializer()).build();
        httpServer.start()
                .thenAccept(ws -> {
                    System.out.println(
                            "WEB server is up! http://localhost:" + ws.getPort());
                })
                .exceptionally(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                    return null;
                });

    }
}
