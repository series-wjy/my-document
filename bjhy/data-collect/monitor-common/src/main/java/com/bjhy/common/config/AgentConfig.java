package com.bjhy.common.config;
import com.bjhy.common.logging.LoggerLevel;
import com.bjhy.common.logging.LoggerType;

/**
 * @ClassName AgentConfig.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月02日 16:25:00
 */
public class AgentConfig {
    public static class Logging {
        public static long MAX_FILE_SIZE = 1024 * 1024 * 200;
        public static LoggerLevel LEVEL = LoggerLevel.DEBUG;
        public static LoggerType TYPE = LoggerType.STDOUT;
        public static String PATH = "";
        public static String FILE_NAME = "monitor-agent.log";
    }

    public static class Agent {
        public static String SERVER_NAME = "monitor-agent";
        public static String SERVER_INSTANCE_ID;
        public static String HOSTNAME;
        public static String IP;
        public static int JVM_COLLECT_INTEVAL = 10;// jvm信息采集间隔，秒
    }

    public static class Remote {
        public static String COLLECTOR_URL = "";
    }
}
