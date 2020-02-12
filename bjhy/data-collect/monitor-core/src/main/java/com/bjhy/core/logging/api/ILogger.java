package com.bjhy.core.logging.api;

/**
 * @ClassName ILogger.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月04日 09:43:12
 */
public interface ILogger {
    void debug(String message);

    void debug(String format, Object... args);

    void info(String message);

    void info(String format, Object... args);

    void warn(String message);

    void warn(String format, Object... args);

    void warn(Throwable throwable, String format);

    void warn(Throwable throwable, String format, Object... args);

    void error(String message);

    void error(String format, Object... args);

    void error(Throwable throwable, String message);

    void error(Throwable throwable, String format, Object... args);

    boolean isDebugEnable();

    boolean isInfoEnable();

    boolean isWarnEnable();

    boolean isErrorEnable();
}
