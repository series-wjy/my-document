/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im.client.handler;

import com.wjy.protocol.packet.Packet;
import com.wjy.protocol.packet.codec.PacketCodec;
import com.wjy.protocol.packet.impl.LoginRequestPacket;
import com.wjy.protocol.packet.impl.LoginResponsePacket;
import com.wjy.protocol.packet.impl.MessageResponsePacket;
import com.wjy.util.LoginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;

import java.util.Date;
import java.util.UUID;

/**
 * @author wangjiayou 2019/7/2
 * @version ORAS v1.0
 */
public class ClientMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;

        Packet packet = PacketCodec.getInstance().decode(byteBuf);

        if(packet instanceof LoginResponsePacket) {
            LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;

            if(loginResponsePacket.isSuccess()) {
                System.out.println(new Date() + "：客户端登录成功！");
                LoginUtil.maskAsLogin(ctx.channel());
            } else {
                System.out.println(new Date() + ": 客户端登录失败，原因：" + loginResponsePacket.getReason());
            }
        } else if(packet instanceof MessageResponsePacket) {
            MessageResponsePacket messageResponsePacket = (MessageResponsePacket) packet;
            if(!StringUtil.isNullOrEmpty(messageResponsePacket.getMsg())) {
                System.out.println(new Date() + "：收到服务器消息回复：[" + messageResponsePacket.getMsg() + "]");
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + "：客户端开始登录......");

        LoginRequestPacket packet = new LoginRequestPacket();
        packet.setUserId(UUID.randomUUID().toString());
        packet.setUsername("GBLW");
        packet.setPassword("gblw");

        ByteBuf byteBuf = PacketCodec.getInstance().encode(ctx.alloc().buffer(), packet);

        ctx.channel().writeAndFlush(byteBuf);
    }
}
