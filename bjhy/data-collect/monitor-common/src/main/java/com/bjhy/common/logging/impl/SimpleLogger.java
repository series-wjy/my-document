package com.bjhy.common.logging.impl;


import com.bjhy.common.config.AgentConfig;
import com.bjhy.common.logging.LoggerLevel;
import com.bjhy.common.logging.WriterFactory;
import com.bjhy.common.logging.api.ILogger;
import com.bjhy.common.util.DateUtil;
import com.bjhy.common.util.ExceptionUtil;
import com.bjhy.common.util.StringUtil;

import java.util.Date;
import java.util.regex.Matcher;

/**
 * @ClassName SimpleLogger.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月04日 13:43:12
 */
public class SimpleLogger implements ILogger {

    private Class<?> targetClass;

    public SimpleLogger(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public void logger(LoggerLevel loggerLevel, String message, Throwable t) {
        WriterFactory.getWriter().write(format(loggerLevel, message, t));
    }

    private String format(LoggerLevel loggerLevel, String message, Throwable t) {
        return StringUtil.join(' ', loggerLevel.name(), DateUtil.getFullFormat(new Date()),
                Thread.currentThread().getName(), targetClass.getSimpleName(), ": ", message, t == null ? "" : ExceptionUtil.format(t));
    }

    private String replaceParam(String format, Object... args) {
        int startSize = 0;
        int paramIndex = 0;
        int index;
        String tmpMessage = format;
        while ((index = format.indexOf("{}", startSize)) != -1) {
            if (paramIndex >= args.length) {
                break;
            }
            /*
             * @Fix Matcher.quoteReplacement:the Illegal group reference issue.
             * exp:"{}".replaceFirst("\\{\\}", "x$")
             */
            tmpMessage = tmpMessage.replaceFirst("\\{\\}", Matcher.quoteReplacement(String.valueOf(args[paramIndex++])));
            startSize = index + 2;
        }
        return tmpMessage;
    }


    @Override
    public void debug(String message) {
        if (isDebugEnable()) {
            logger(LoggerLevel.DEBUG, message, null);
        }
    }

    @Override
    public void debug(String format, Object... args) {
        if (isDebugEnable()) {
            logger(LoggerLevel.DEBUG, replaceParam(format, args), null);
        }
    }

    @Override
    public void info(String message) {
        if (isInfoEnable()) {
            logger(LoggerLevel.INFO, message, null);
        }
    }

    @Override
    public void info(String format, Object... args) {
        if (isInfoEnable()) {
            logger(LoggerLevel.INFO, replaceParam(format, args), null);
        }
    }

    @Override
    public void warn(String message) {
        if (isWarnEnable()) {
            logger(LoggerLevel.WARN, message, null);
        }
    }

    @Override
    public void warn(String format, Object... args) {
        if (isWarnEnable()) {
            logger(LoggerLevel.WARN, replaceParam(format, args), null);
        }
    }

    @Override
    public void warn(Throwable t, String message) {
        if (isWarnEnable()) {
            logger(LoggerLevel.WARN, message, t);
        }
    }

    @Override
    public void warn(Throwable t, String format, Object... args) {
        if (isWarnEnable()) {
            logger(LoggerLevel.WARN, replaceParam(format, args), t);
        }
    }

    @Override
    public void error(String message) {
        if (isErrorEnable()) {
            logger(LoggerLevel.ERROR, message, null);
        }
    }

    @Override
    public void error(String format, Object... args) {
        if (isErrorEnable()) {
            logger(LoggerLevel.ERROR, replaceParam(format, args), null);
        }
    }

    @Override
    public void error(Throwable t, String message) {
        if (isErrorEnable()) {
            logger(LoggerLevel.ERROR, message, t);
        }
    }

    @Override
    public void error(Throwable t, String format, Object... args) {
        if (isErrorEnable()) {
            logger(LoggerLevel.ERROR, replaceParam(format, args), t);
        }
    }

    @Override
    public boolean isDebugEnable() {
        return LoggerLevel.DEBUG.compareTo(AgentConfig.Logging.LEVEL) >= 0;
    }

    @Override
    public boolean isInfoEnable() {
        return LoggerLevel.INFO.compareTo(AgentConfig.Logging.LEVEL) >= 0;
    }

    @Override
    public boolean isWarnEnable() {
        return LoggerLevel.WARN.compareTo(AgentConfig.Logging.LEVEL) >= 0;
    }

    @Override
    public boolean isErrorEnable() {
        return LoggerLevel.ERROR.compareTo(AgentConfig.Logging.LEVEL) >= 0;
    }
}
