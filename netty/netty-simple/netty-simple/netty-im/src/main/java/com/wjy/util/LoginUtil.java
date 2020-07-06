/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;

/**
 * @author wangjiayou 2019/7/3
 * @version ORAS v1.0
 */
public class LoginUtil {

    /**
     * 设置登录标识
     * @auther wangjiayou
     * @date 2019/7/3
     * @param channel
     * @return void
     */
    public static void maskAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    /**
     * 判断登录
     * @auther wangjiayou
     * @date 2019/7/3
     * @param channel
     * @return boolean
     */
    public static boolean hasLogin(Channel channel) {
        Attribute<Boolean> attribute = channel.attr(Attributes.LOGIN);
        return attribute.get() != null;
    }
}
