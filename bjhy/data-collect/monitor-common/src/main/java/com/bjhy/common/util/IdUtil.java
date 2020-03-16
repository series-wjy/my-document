package com.bjhy.common.util;

import java.util.UUID;

/**
 * @ClassName IdUtil.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月03日 15:43:51
 */
public class IdUtil {
    /**
     * 获取ID.
     *
     * @return
     */
    public static final String getId() {
        return UUID.randomUUID().toString();
    }
}
