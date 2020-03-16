package com.bjhy.common.logging;

import com.bjhy.common.config.AgentConfig;
import com.bjhy.common.logging.api.ILogger;
import com.bjhy.common.logging.impl.NoopLogger;
import com.bjhy.common.logging.impl.SimpleLogger;

/**
 * @ClassName LoggerFactory.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月04日 14:01:10
 */
public class LoggerFactory {
    /**
     * 获取日志对象.
     *
     * @param clazz 日志来源类
     * @return
     */
    public static ILogger getLogger(Class<?> clazz) {
        if (AgentConfig.Logging.LEVEL == LoggerLevel.OFF) {
            return NoopLogger.INSTANCE;
        } else {
            return new SimpleLogger(clazz);
        }
    }
}
