/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.server.handler;

import com.wjy.im2.session.Session;
import com.wjy.im2.session.SessionUtil;
import com.wjy.protocol.packet.impl.QuitGroupRequestPacket;
import com.wjy.protocol.packet.impl.QuitGroupResponsePacket;
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
public class QuitGroupHandler extends SimpleChannelInboundHandler<QuitGroupRequestPacket> {
    public static final QuitGroupHandler INSTANCE = new QuitGroupHandler();

    private QuitGroupHandler() {

    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QuitGroupRequestPacket msg) throws Exception {
        String groupId = msg.getGroupId();
        ChannelGroup group = SessionUtil.getChannelGroup(groupId);

        if(group != null) {
            group.remove(ctx.channel());

            if(group.size() == 0) {
                SessionUtil.cleanGroupInfo(groupId);
            }
        }

        QuitGroupResponsePacket response = new QuitGroupResponsePacket();
        response.setGroupId(groupId);
        response.setSuccess(true);
        ctx.channel().writeAndFlush(response);

        Session session = SessionUtil.getSession(ctx.channel());

        // 退出群聊其他组员通知
        response.setQuitUserId(session.getUserId());
        response.setQuitUserName(session.getUserName());
        group.writeAndFlush(response, channel -> {
            if(channel.equals(ctx.channel())) {
                return false;
            } else {
                return true;
            }
        });
        LogUtil.print("退出群聊[userId:" + session.getUserId() + " userName:" + session.getUserName() + "]");
    }
}
