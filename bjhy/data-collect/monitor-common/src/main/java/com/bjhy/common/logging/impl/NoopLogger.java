package com.bjhy.common.logging.impl;


import com.bjhy.common.logging.api.ILogger;

/**
 * @ClassName NoopLogger.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月04日 16:45:12
 */
public enum NoopLogger implements ILogger {
    INSTANCE;

    @Override
    public void debug(String message) {

    }

    @Override
    public void debug(String format, Object... args) {

    }

    @Override
    public void info(String message) {

    }

    @Override
    public void info(String format, Object... args) {

    }

    @Override
    public void warn(String message) {

    }

    @Override
    public void warn(String format, Object... args) {

    }

    @Override
    public void warn(Throwable throwable, String format) {

    }

    @Override
    public void warn(Throwable throwable, String format, Object... args) {

    }

    @Override
    public void error(String message) {

    }

    @Override
    public void error(String format, Object... args) {

    }

    @Override
    public void error(Throwable throwable, String message) {

    }

    @Override
    public void error(Throwable throwable, String format, Object... args) {

    }

    @Override
    public boolean isDebugEnable() {
        return false;
    }

    @Override
    public boolean isInfoEnable() {
        return false;
    }

    @Override
    public boolean isWarnEnable() {
        return false;
    }

    @Override
    public boolean isErrorEnable() {
        return false;
    }
}