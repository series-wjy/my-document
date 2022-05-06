package com.wjy.simple.entity;

import java.sql.ResultSet;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年02月21日 15:55:00
 */
public class ResponseSample {
    private String ok;
    private String data;
    private long currentTimeMillis;
    public ResponseSample(String ok, String data, long currentTimeMillis) {
        this.ok = ok;
        this.data = data;
        this.currentTimeMillis = currentTimeMillis;
    }

    public String getCode() {
        return ok;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
        return currentTimeMillis;
    }
}
