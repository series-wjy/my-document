/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.session;


import com.wjy.util.Attributes;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangjiayou 2019/7/8
 * @version ORAS v1.0
 */
public class SessionUtil {

    private static Map<String, Channel> sessionMap = new ConcurrentHashMap<>();

    private static Map<String, ChannelGroup> groupMap = new ConcurrentHashMap<>();

    /**
     * 绑定session
     * @auther wangjiayou
     * @date 2019/7/8
     * @param session
     * @param channel
     * @return void
     */
    public static void bindSession(Session session, Channel channel) {
        sessionMap.put(session.getUserId(), channel);
        channel.attr(Attributes.SESSION).set(session);
    }

    /**
     * 解绑session
     * @auther wangjiayou
     * @date 2019/7/8
     * @param channel
     * @return void
     */
    public static void unBindSession(Channel channel) {
        if(hasLogin(channel)) {
            sessionMap.remove(getSession(channel).getUserId());
            channel.attr(Attributes.SESSION).set(null);
        }

    }

    /**
     * 加入群聊
     * @auther wangjiayou
     * @date 2019/7/9
     * @param groupId
     * @return void
     */
    public static ChannelGroup getChannelGroup(String groupId) {
        return groupMap.get(groupId);
    }

    public static ChannelGroup cleanGroupInfo(String groupId) {
        return groupMap.remove(groupId);
    }

    public static ChannelGroup setChannelGroup(String groupId, ChannelGroup channelGroup) {
        return groupMap.put(groupId, channelGroup);
    }

    public static Session getSession(Channel channel) {
        return channel.attr(Attributes.SESSION).get();
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(Attributes.SESSION);
    }

    /**
     * 获取连接通道
     * @auther wangjiayou
     * @date 2019/7/8
     * @param userId
     * @return io.netty.channel.Channel
     */
    public static Channel getChannel(String userId) {
        return sessionMap.get(userId);
    }
}
