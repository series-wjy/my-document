/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.client.handler;

import com.wjy.protocol.packet.impl.SendToGroupResponsePacket;
import com.wjy.util.LogUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.StringUtil;

/**
 * @author wangjiayou 2019/7/3
 * @version ORAS v1.0
 */
public class SendToGroupResponseHandler extends SimpleChannelInboundHandler<SendToGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SendToGroupResponsePacket msg) throws Exception {
        if(!StringUtil.isNullOrEmpty(msg.getMsg())) {
            String fromUserId = msg.getFromUserId();
            String fromUserName = msg.getFromUserName();
            LogUtil.print("收到群消息[fromUserId:" + fromUserId + " fromUserName:" + fromUserName + "]:" + msg.getMsg());
        }
    }
}
