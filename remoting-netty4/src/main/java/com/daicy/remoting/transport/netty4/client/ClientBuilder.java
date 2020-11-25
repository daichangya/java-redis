package com.daicy.remoting.transport.netty4.client;

import com.daicy.remoting.transport.netty4.DefaultServerContext;
import com.daicy.remoting.transport.netty4.ServerBuilder;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @date:19-11-13
 */
public class ClientBuilder extends ServerBuilder {

    private String host;

    private long requestTimeOut = 10000;

    private Timer timer;

    private ClientBuilder(String host,int port) {
        super(port);
        this.host = host;
    }

    public static ClientBuilder forHostPort(String host,int port) {
        return new ClientBuilder(host,port);
    }

    public String getHost() {
        return host;
    }

    public ClientBuilder setTimer(Timer timer) {
        this.timer = timer;
        return this;
    }

    public Timer getTimer() {
        return timer;
    }

    public long getRequestTimeOut() {
        return requestTimeOut;
    }

    public ClientBuilder setRequestTimeOut(long requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
        return this;
    }

    @Override
    public Client build() {
        if (getMinWorkers() > getMaxWorkers()) {
            throw new IllegalArgumentException("minWorkers is greater than maxWorkers");
        }

        if (timer == null) {
            this.timer = new HashedWheelTimer();
        }

        if(null == getServerContext()){
            this.setServerContext(new DefaultServerContext());
        }

        return new ClientImpl(this);
    }
}

