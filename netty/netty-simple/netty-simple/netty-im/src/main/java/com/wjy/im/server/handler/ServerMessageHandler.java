/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im.server.handler;

import com.wjy.protocol.packet.Packet;
import com.wjy.protocol.packet.codec.PacketCodec;
import com.wjy.protocol.packet.impl.LoginRequestPacket;
import com.wjy.protocol.packet.impl.LoginResponsePacket;
import com.wjy.protocol.packet.impl.MessageRequestPacket;
import com.wjy.protocol.packet.impl.MessageResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @author wangjiayou 2019/7/2
 * @version ORAS v1.0
 */
public class ServerMessageHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(new Date() + "：服务端接受登录消息......");

        ByteBuf byteBuf = (ByteBuf) msg;
        Packet packet = PacketCodec.getInstance().decode(byteBuf);
        if(packet instanceof LoginRequestPacket) {
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;

            LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
            loginResponsePacket.setVersion(loginRequestPacket.getVersion());
            if(valid(loginRequestPacket)) {
                loginResponsePacket.setSuccess(true);
            } else {
                loginResponsePacket.setSuccess(false);
                loginResponsePacket.setReason("账号或密码效验失败！");
            }

            ByteBuf response = PacketCodec.getInstance().encode(loginResponsePacket);
            ctx.channel().writeAndFlush(response);
        } else if(packet instanceof MessageRequestPacket) {
            MessageRequestPacket messageRequestPacket = (MessageRequestPacket) packet;
            System.out.println(new Date() + "：服务端收到消息[" + messageRequestPacket.getMsg() + "]");

            MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
            messageResponsePacket.setMsg(messageRequestPacket.getMsg());
            ByteBuf response = PacketCodec.getInstance().encode(messageResponsePacket);
            ctx.channel().writeAndFlush(response);
        }
    }

    private boolean valid(LoginRequestPacket packet) {
        return true;
    }
}
