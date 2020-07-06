/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.client.handler;

import com.wjy.protocol.packet.impl.CreateGroupResponsePacket;
import com.wjy.protocol.packet.impl.SendToUserResponsePacket;
import com.wjy.util.LogUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.StringUtil;

/**
 * @author wangjiayou 2019/7/3
 * @version ORAS v1.0
 */
public class CreateGroupResponseHandler extends SimpleChannelInboundHandler<CreateGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupResponsePacket msg) throws Exception {
        if(!StringUtil.isNullOrEmpty(msg.getGroupId())) {
            String groupId = msg.getGroupId();
            String groupName = msg.getGroupName();
            LogUtil.print("收到群创建成功消息[groupId:" + groupId + " groupName:" + groupName + "]");
        }
    }
}
