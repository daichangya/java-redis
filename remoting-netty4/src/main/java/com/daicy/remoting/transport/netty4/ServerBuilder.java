package com.daicy.remoting.transport.netty4;

import io.netty.channel.ChannelInitializer;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @date:19-11-13
 */
public class ServerBuilder {


    private String sssionCookieName = StringUtils.EMPTY;

    private int backlog = Constant.DEFAULT_BACKLOG;

    private int acceptors = Constant.DEFAULT_ACCEPTOR_COUNT;
    private int ioWorkers = Constant.DEFAULT_IO_WORKER_COUNT;

    private int minWorkers = Constant.DEFAULT_MIN_WORKER_THREAD;
    private int maxWorkers = Constant.DEFAULT_MAX_WORKER_THREAD;

    private int maxConnection = Integer.MAX_VALUE;
    private int maxPendingRequest = Constant.DEFAULT_MAX_PENDING_REQUEST;
    private int maxIdleTime = Constant.DEFAULT_MAX_IDLE_TIME;

    // 以下参数用于accept到服务器的socket
    private int sendBuffer = Constant.DEFAULT_SEND_BUFFER_SIZE;
    private int recvBuffer = Constant.DEFAULT_RECV_BUFFER_SIZE;

    private int maxPacketLength = Constant.DEFAULT_MAX_PACKET_LENGTH;

    private int sessionTimeout = 60 * 60; // 1 hour

    private boolean devMode;

    private String contextPath = StringUtils.EMPTY;


    private int port;
    private InetAddress inetAddress;

    private ChannelInitializer channelInitializer;

    private ServerBuilder(int port) {
        this.port = port;
    }

    public static ServerBuilder forPort(int port) {
        return new ServerBuilder(port);
    }

    public ServerBuilder channelInitializer(ChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
        return this;
    }

    public ServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ServerBuilder inetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }

    public ServerBuilder sessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }


    public ServerBuilder backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public ServerBuilder acceptors(int acceptors) {
        this.acceptors = acceptors;
        return this;
    }

    public ServerBuilder ioWorkers(int ioWorkers) {
        this.ioWorkers = ioWorkers;
        return this;
    }

    public ServerBuilder minWorkers(int minWorkers) {
        this.minWorkers = minWorkers;
        return this;
    }

    public ServerBuilder maxWorkers(int maxWorkers) {
        this.maxWorkers = maxWorkers;
        return this;
    }

    public ServerBuilder maxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        return this;
    }

    public ServerBuilder sendBuffer(int sendBuffer) {
        this.sendBuffer = sendBuffer;
        return this;
    }

    public ServerBuilder recvBuffer(int recvBuffer) {
        this.recvBuffer = recvBuffer;
        return this;
    }

    public ServerBuilder maxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
        return this;
    }

    public ServerBuilder maxPendingRequest(int maxPendingRequest) {
        this.maxPendingRequest = maxPendingRequest;
        return this;
    }

    public ServerBuilder maxPacketLength(int maxPacketLength) {
        this.maxPacketLength = maxPacketLength;
        return this;
    }


    public ServerBuilder devMode(boolean devMode) {
        this.devMode = devMode;
        return this;
    }


    public ServerBuilder contextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public ServerBuilder sssionCookieName(String sssionCookieName) {
        this.sssionCookieName = sssionCookieName;
        return this;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public int getBacklog() {
        return backlog;
    }

    public int getAcceptors() {
        return acceptors;
    }

    public int getIoWorkers() {
        return ioWorkers;
    }

    public int getMinWorkers() {
        return minWorkers;
    }

    public int getMaxWorkers() {
        return maxWorkers;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public int getMaxPendingRequest() {
        return maxPendingRequest;
    }

    public boolean isDevMode() {
        return devMode;
    }


    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public int getSendBuffer() {
        return sendBuffer;
    }

    public int getRecvBuffer() {
        return recvBuffer;
    }

    public int getMaxPacketLength() {
        return maxPacketLength;
    }

    public int getPort() {
        return port;
    }

    public ChannelInitializer getChannelInitializer() {
        return channelInitializer;
    }

    public Server build() {
        if (minWorkers > maxWorkers) {
            throw new IllegalArgumentException("minWorkers is greater than maxWorkers");
        }

        if (maxPendingRequest <= 0) {
            throw new IllegalArgumentException("maxPendingRequest must be greater than 0");
        }

        return new ServerImpl(this);
    }
}

