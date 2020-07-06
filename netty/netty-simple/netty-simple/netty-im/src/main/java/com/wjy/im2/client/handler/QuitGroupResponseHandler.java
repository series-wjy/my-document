/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.client.handler;

import com.wjy.protocol.packet.impl.QuitGroupResponsePacket;
import com.wjy.util.LogUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wangjiayou 2019/7/11
 * @version ORAS v1.0
 */
public class QuitGroupResponseHandler extends SimpleChannelInboundHandler<QuitGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QuitGroupResponsePacket msg) throws Exception {
        if(msg.getQuitUserId() != null) {
            LogUtil.print("用户[userId:" + msg.getQuitUserId() + " userName:"
                    + msg.getQuitUserName() + "]退出群聊[groupId：" + msg.getGroupId() + "]");
        } else {
            LogUtil.print("退出群聊[groupId:" + msg.getGroupId() + "]成功");
        }
    }
}
