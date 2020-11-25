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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec(4096, 8192, 8192, false));
        // Uncomment the following line if you don't want to handle HttpChunks.
        pipeline.addLast(new HttpObjectAggregator(100 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
//        pipeline.addLast(new HttpStaticFileServerHandler());

//        pipeline.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new IdleStateHandler(5, 0, 0));
        pipeline.addLast(new HttpServerHandler());
    }
}
