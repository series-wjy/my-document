package com.bjhy.common.logging.impl;

import com.bjhy.common.config.AgentConfig;
import com.bjhy.common.logging.api.IWriter;
import com.bjhy.common.util.Constants;
import com.bjhy.common.util.DateUtil;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;


/**
 * @ClassName FileWriter.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月04日 13:19:12
 */
public class FileWriter implements IWriter, EventHandler<FileWriter.LoggerEvent> {
    private static FileWriter INSTANCE;
    private static final Object CREATE_LOCK = new Object();

    private FileOutputStream fileOutputStream;
    private Disruptor<LoggerEvent> disruptor;
    private RingBuffer<LoggerEvent> buffer;
    private volatile boolean started = false;
    private volatile long lineNum;
    private volatile long fileSize;

    private FileWriter() {
        disruptor = new Disruptor<LoggerEvent>(new EventFactory<LoggerEvent>() {
            @Override
            public LoggerEvent newInstance() {
                return new LoggerEvent();
            }
        }, 1024, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(this);
        buffer = disruptor.getRingBuffer();
        lineNum = 0;
        disruptor.start();
    }

    /**
     * 获取单例对象.
     *
     * @return
     */
    public static IWriter getInstance() {
        if (INSTANCE == null) {
            synchronized (CREATE_LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new FileWriter();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 日志输出.
     *
     * @param message 日志内容
     */
    @Override
    public void write(String message) {
        // 如果日志的buffer满了，则抛弃日志，避免应用阻塞
        if (buffer.remainingCapacity() == 0) {
            return;
        }
        long next = buffer.next();
        try {
            LoggerEvent logEvent = buffer.get(next);
            logEvent.setMessage(message);
        } finally {
            buffer.publish(next);
        }
    }

    private void write(String message, boolean forceFlush) {
        try {
            fileOutputStream.write(message.getBytes());
            fileSize += message.getBytes().length;
            if (forceFlush || lineNum % 20 == 0) {
                fileOutputStream.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            checkFile();
        }
    }

    @Override
    public void onEvent(LoggerEvent logEvent, long sequence, boolean endOfBatch) throws Exception {
        if (hasWriterStream()) {
            try {
                lineNum++;
                write(logEvent.getMessage() + Constants.LINE_SEPARATOR, endOfBatch);
            } finally {
                logEvent.setMessage(null);
            }
        }
    }

    private void checkFile() {
        if (fileSize > AgentConfig.Logging.MAX_FILE_SIZE) {
            forceExecute(new Callable() {
                @Override
                public Object call() throws Exception {
                    fileOutputStream.flush();
                    return null;
                }
            });
            forceExecute(new Callable() {
                @Override
                public Object call() throws Exception {
                    fileOutputStream.close();
                    return null;
                }
            });
            forceExecute(new Callable() {
                @Override
                public Object call() throws Exception {
                    new File(AgentConfig.Logging.PATH, AgentConfig.Logging.FILE_NAME)
                            .renameTo(new File(AgentConfig.Logging.PATH,
                                    AgentConfig.Logging.FILE_NAME + "." + DateUtil.getFormatForFile(new Date())));
                    return null;
                }
            });
            forceExecute(new Callable() {
                @Override
                public Object call() throws Exception {
                    fileOutputStream = null;
                    started = false;
                    return null;
                }
            });
        }
    }

    private void forceExecute(Callable callable) {
        try {
            callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasWriterStream() {
        if (fileOutputStream != null) {
            return true;
        }
        if (!started) {
            File logFilePath = new File(AgentConfig.Logging.PATH);
            if (!logFilePath.exists()) {
                logFilePath.exists();
            } else if (!logFilePath.isDirectory()) {
                System.err.println("Log dir(" + AgentConfig.Logging.PATH + ") is not a directory.");
            }
            try {
                File logFile = new File(AgentConfig.Logging.PATH + AgentConfig.Logging.FILE_NAME);
                fileOutputStream = new FileOutputStream(logFile, true);
                fileSize = logFile.length();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            started = true;
        }
        return fileOutputStream != null;
    }

    public static class LoggerEvent {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
