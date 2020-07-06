/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im.client;

import com.wjy.im.client.handler.ClientMessageHandler;
import com.wjy.protocol.packet.codec.PacketCodec;
import com.wjy.protocol.packet.impl.MessageRequestPacket;
import com.wjy.util.LoginUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.Scanner;

/**
 * @author wangjiayou 2019/7/2
 * @version ORAS v1.0
 */
public class IMessageClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientMessageHandler());
                    }
                });
        bootstrap.connect("127.0.0.1", 8000).addListener(future -> {
            if(future.isSuccess()) {
                System.out.println(new Date() + "：连接[127.0.0.1:8000]成功！");
                Channel channel = ((ChannelFuture)future).channel();
                startConsoleThread(channel);
            } else {
                System.out.println(new Date() + "：连接[127.0.0.1:8000]失败！");
            }
        });
    }

    private static void startConsoleThread(Channel channel) {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                if(LoginUtil.hasLogin(channel)) {
                    System.out.println("输入消息发送至服务端：");
                    Scanner scanner = new Scanner(System.in);
                    String line = scanner.nextLine();

                    MessageRequestPacket messageRequestPacket = new MessageRequestPacket();
                    messageRequestPacket.setMsg(line);
                    ByteBuf byteBuf = PacketCodec.getInstance().encode(messageRequestPacket);
                    channel.writeAndFlush(byteBuf);
                }
            }
        }).start();
    }
}
