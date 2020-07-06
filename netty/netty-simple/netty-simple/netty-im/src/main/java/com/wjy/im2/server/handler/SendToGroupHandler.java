/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.server.handler;

import com.wjy.im2.session.Session;
import com.wjy.im2.session.SessionUtil;
import com.wjy.protocol.packet.impl.SendToGroupRequestPacket;
import com.wjy.protocol.packet.impl.SendToGroupResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

/**
 * @author wangjiayou 2019/7/11
 * @version ORAS v1.0
 */
@ChannelHandler.Sharable
public class SendToGroupHandler extends SimpleChannelInboundHandler<SendToGroupRequestPacket> {
    public static final SendToGroupHandler INSTANCE = new SendToGroupHandler();

    private SendToGroupHandler() {

    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SendToGroupRequestPacket msg) throws Exception {
        String groupId = msg.getGroupId();
        Session session = SessionUtil.getSession(ctx.channel());

        SendToGroupResponsePacket response = new SendToGroupResponsePacket();
        response.setFromGroupId(groupId);
        response.setFromUserId(session.getUserId());
        response.setFromUserName(session.getUserName());
        response.setMsg(msg.getMsg());

        ChannelGroup group = SessionUtil.getChannelGroup(groupId);
        if(group != null) {
            group.writeAndFlush(response);
        }
    }
}
