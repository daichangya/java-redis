package com.daicy.redis.client;

import com.daicy.redis.client.codec.ReplyDecoder;
import com.daicy.redis.client.codec.ReplyEncoder;
import com.daicy.redis.client.handler.RedisClientHandler;
import com.daicy.redis.protocal.MultiBulkRedisMessage;
import com.daicy.redis.protocal.RedisMessage;
import com.daicy.remoting.transport.netty4.client.Client;
import com.daicy.remoting.transport.netty4.client.ClientBuilder;
import com.daicy.remoting.transport.netty4.client.ClientCallback;
import com.daicy.remoting.transport.netty4.client.ClientPromise;
import com.daicy.remoting.transport.netty4.client.handler.ClientInitializer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.remoting.transport.netty4
 * @date:11/23/20
 */
@Slf4j
public class RedisClient implements MessageCallback {


    private final Client client;

    public RedisClient() throws Exception {
        this(null);
    }

    public RedisClient(ClientBuilder clientBuilder) throws Exception {
        if (null == clientBuilder) {
            clientBuilder = ClientBuilder.forHostPort("localhost", 6379);
        }
        clientBuilder.channelInitializer(new ClientInitializer(this));
        client = start(clientBuilder);
    }

    private Client start(ClientBuilder clientBuilder) throws InterruptedException, java.util.concurrent.ExecutionException, IOException {
        Client client = clientBuilder.build();
        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));
        client.init();
        client.start().get();
        return client;
    }


    public Client getClient() {
        return client;
    }

    @Override
    public void channel(SocketChannel channel) {
        ChannelPipeline p = channel.pipeline();
        p.addLast("redisDecoder",new RedisDecoder());
        p.addLast(new RedisBulkStringAggregator());
        p.addLast(new RedisArrayAggregator());
        p.addLast(new ReplyDecoder());
        p.addLast(new RedisEncoder());
        p.addLast(new ReplyEncoder());
        p.addLast(new RedisClientHandler(this));
    }

    @Override
    public void onConnect() {
        System.out.println("connected!");
        ping();
    }

    public void shutdown() {
        client.shutdown();
    }

    private void ping() {
//        client.getClientBuilder().getTimer().newTimeout(new TimerTask() {
//            @Override
//            public void run(Timeout timeout) throws Exception {
//                String[] commands = "ping".split("\\s+");
//                RedisMessage redisMessage =
//                        new MultiBulkRedisMessage(asList(commands).stream().map(RedisMessage::string).collect(toList()));
//                ClientPromise<RedisMessage> promise = send(redisMessage, -1);
//                log.info(new String(promise.get().encode()));
//                ping();
//            }
//        }, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDisconnect() {
        client.reconnect();
        System.out.println("disconnected! 88");
    }

    @Override
    public void onMessage(RedisMessage redisMessage) {
        System.out.println(new String(redisMessage.encode()));
    }

    public <T> ClientPromise sendMessage(String message){
        String[] commands = message.split("\\s+");
        com.daicy.redis.protocal.RedisMessage redisMessage =
                new MultiBulkRedisMessage(asList(commands).stream().map(com.daicy.redis.protocal.RedisMessage::string).collect(toList()));
        ClientPromise promise = send(commands[0],redisMessage,999999);
        return promise;
    }


    public <T> ClientPromise send(String commandName,T redisMessage, long timeout) {
        ClientPromise promise = new ClientPromise();
        if (timeout == -1) {
            timeout = client.getClientBuilder().getSessionTimeout();
        }

        if (client.getEventLoopGroup().isShuttingDown()) {
            RedisException cause = new RedisException("Redis is shutdown");
            return ClientPromise.newFailedFuture((Throwable) cause);
        }

        ScheduledFuture<?> scheduledFuture = client.getEventLoopGroup().schedule(new Runnable() {
            @Override
            public void run() {
                RedisException ex = new RedisException("Command execution timeout for command: "
                        + redisMessage
                        + ", Redis client: " + client.getClientBuilder().getHost());
                promise.tryFailure(ex);
            }
        }, timeout, TimeUnit.MILLISECONDS);

        promise.onComplete((res, e) -> {
            scheduledFuture.cancel(false);
        });

        ChannelFuture writeFuture = send(new RedisCommand(commandName,redisMessage, promise));
        writeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    promise.tryFailure(future.cause());
                }
            }
        });
        return promise;
    }

    public ChannelFuture send(RedisCommand redisCommand) {
        return getClient().getChannel().writeAndFlush(redisCommand);
    }

}