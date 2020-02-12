package com.bjhy.core.util;

/**
 * @ClassName Constants.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月02日 15:47:11
 */
public class Constants {
    /**
     * 换行符.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    /**
     * 分布式传播的Key.
     */
    public static final String CARRIER_KEY = "SA_CONTEXT";

    public static class Tag {
        public static final String TAG_COMPONENT = "component";
        public static final String TAG_HTTP_METHOD = "httpMethod";
        public static final String TAG_HTTP_URL = "httpUrl";
        public static final String TAG_ERROR = "error";
        public static final String TAG_ERROR_MESSAGE = "errorMessage";
        public static final String TAG_ERROR_STACK = "errorStack";
        public static final String TAG_RPC_URL = "rpcUrl";
        public static final String TAG_PEER_ADDRESS = "peerAddress";
        public static final String TAG_ORDER = "order";
        public static final String TAG_ARGUMENTS = "arguments";
    }


    public static class Baggage {
        public static final String BAGGAGE_ORDER = "order";
    }
}
