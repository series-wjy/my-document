package com.bjhy.core.logging;

import com.bjhy.core.config.AgentConfig;
import com.bjhy.core.logging.api.ILogger;
import com.bjhy.core.logging.impl.NoopLogger;
import com.bjhy.core.logging.impl.SimpleLogger;

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
