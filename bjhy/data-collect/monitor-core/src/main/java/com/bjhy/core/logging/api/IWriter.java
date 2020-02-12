package com.bjhy.core.logging.api;

/**
 * @ClassName IWriter.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月04日 09:45:33
 */
public interface IWriter {
    /**
     * 输出日志.
     * @param message  日志内容
     */
    void write(String message);
}
