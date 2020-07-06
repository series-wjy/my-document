/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.server.handler;

import com.wjy.im2.session.Session;
import com.wjy.im2.session.SessionUtil;
import com.wjy.protocol.packet.impl.JoinGroupRequestPacket;
import com.wjy.protocol.packet.impl.JoinGroupResponsePacket;
import com.wjy.util.LogUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

/**
 * @author wangjiayou 2019/7/11
 * @version ORAS v1.0
 */
@ChannelHandler.Sharable
public class JoinGroupHandler extends SimpleChannelInboundHandler<JoinGroupRequestPacket> {
    public static final JoinGroupHandler INSTANCE = new JoinGroupHandler();

    private JoinGroupHandler() {

    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinGroupRequestPacket msg) throws Exception {
        String groupId = msg.getGroupId();
        ChannelGroup group = SessionUtil.getChannelGroup(groupId);

        if(group != null) {
            group.add(ctx.channel());
        }

        JoinGroupResponsePacket response = new JoinGroupResponsePacket();
        response.setGroupId(groupId);
        response.setSuccess(true);
        ctx.channel().writeAndFlush(response);

        Session session = SessionUtil.getSession(ctx.channel());
        // 加入群聊其他组员通知
        response.setJoinUserId(session.getUserId());
        response.setJoinUserName(session.getUserName());
        group.writeAndFlush(response, channel -> {
            if(channel.equals(ctx.channel())) {
                return false;
            } else {
                return true;
            }
        });

        LogUtil.print("加入群聊[userId:" + session.getUserId() + " userName:" + session.getUserName() + "]");
    }
}
