/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.client.handler;

import com.wjy.protocol.packet.impl.JoinGroupResponsePacket;
import com.wjy.util.LogUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wangjiayou 2019/7/11
 * @version ORAS v1.0
 */
public class JoinGroupResponseHandler extends SimpleChannelInboundHandler<JoinGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinGroupResponsePacket msg) throws Exception {
        if(msg.getJoinUserId() != null) {
            LogUtil.print("用户[userId:" + msg.getJoinUserId() + " userName:"
                    + msg.getJoinUserName() + "]加入群聊[groupId：" + msg.getGroupId() + "]");
        } else {
            LogUtil.print("加入群聊[groupId:" + msg.getGroupId() + "]成功");
        }
    }
}
