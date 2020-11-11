package com.daicy.remoting.transport.netty4;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @date:19-11-13
 */
public class Constant {
    public static final int NCPU = Runtime.getRuntime().availableProcessors();

    public static final int DEFAULT_SEND_BUFFER_SIZE = 256 * 1024;

    public static final int DEFAULT_RECV_BUFFER_SIZE = 256 * 1024;

    public static final int DEFAULT_CONNECTION_SIZE = 1;

    public static final int DEFAULT_ACCEPTOR_COUNT = NCPU * 2;

    public static final int DEFAULT_IO_WORKER_COUNT = NCPU * 2;

    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;

    public static final int DEFAULT_SO_TIMEOUT = 3000;

    public static final int DEFAULT_MIN_WORKER_THREAD = 100;

    public static final int DEFAULT_MAX_WORKER_THREAD = 100;

    public static final int DEFAULT_BACKLOG = 1024;

    public static final int DEFAULT_MAX_PENDING_REQUEST = 10000;

    public static final String SIGN_COLON = ":";

    public static final int DEFAULT_MAX_IDLE_TIME = 10;

    public static final int MAX_FRAME_LENGTH = 1048576 * 5;

    public static final String SERVER_NAME = "NettyServer";

    public static final int DEFAULT_MAX_PACKET_LENGTH = 1024 * 1024;

    public static final String BLANK_STRING = "";

    public static final int DEFAULT_LISTEN_PORT = 8080;

    public static final String CLASS_SUFFIX = ".class";

    public static final int CLASS_SUFFIX_LENGTH = 6;
}
