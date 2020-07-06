/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.server.handler;

import com.wjy.im2.session.Session;
import com.wjy.im2.session.SessionUtil;
import com.wjy.protocol.packet.impl.SendToUserRequestPacket;
import com.wjy.protocol.packet.impl.SendToUserResponsePacket;
import com.wjy.util.LogUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wangjiayou 2019/7/3
 * @version ORAS v1.0
 */
@ChannelHandler.Sharable
public class SendToUserHandler extends SimpleChannelInboundHandler<SendToUserRequestPacket> {

    public static final SendToUserHandler INSTANCE = new SendToUserHandler();

    private SendToUserHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SendToUserRequestPacket msg) throws Exception {
        LogUtil.print("服务端收到消息[" + msg.getMsg() + "]");

        // 1、获取消息发送方的会话信息
        Session session = SessionUtil.getSession(ctx.channel());

        // 2、通过消息发送方的会话信息构造要发送的消息
        SendToUserResponsePacket sendToUserResponsePacket = new SendToUserResponsePacket();
        sendToUserResponsePacket.setFromUserId(session.getUserId());
        sendToUserResponsePacket.setFromUserName(session.getUserName());
        sendToUserResponsePacket.setMsg(msg.getMsg());

        // 3、获取消息接收方的Channel
        Channel toUserChannel = SessionUtil.getChannel(msg.getToUserId());

        // 4、将消息转发给消息接收方
        if(toUserChannel != null && SessionUtil.hasLogin(toUserChannel)) {
            toUserChannel.writeAndFlush(sendToUserResponsePacket);
        } else {
            sendToUserResponsePacket.setMsg("对方不在线，请稍后重试！");
            ctx.writeAndFlush(sendToUserResponsePacket);
        }
    }
}
