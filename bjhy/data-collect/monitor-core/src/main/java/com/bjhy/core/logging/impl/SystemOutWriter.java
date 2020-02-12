package com.bjhy.core.logging.impl;


import com.bjhy.core.logging.api.IWriter;

/**
 * @ClassName SystemOutWriter.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月04日 14:11:12
 */
public enum SystemOutWriter implements IWriter {
    INSTANCE;

    @Override
    public void write(String message) {
        System.out.println(message);
    }
}