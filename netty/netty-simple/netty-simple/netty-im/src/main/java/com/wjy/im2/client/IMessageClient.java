/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.client;

import com.wjy.command.impl.ConsoleCommandMannager;
import com.wjy.command.impl.LoginConsoleCommand;
import com.wjy.im2.client.handler.CreateGroupResponseHandler;
import com.wjy.im2.client.handler.GroupMembersResponseHandler;
import com.wjy.im2.client.handler.JoinGroupResponseHandler;
import com.wjy.im2.client.handler.LoginResponseHandler;
import com.wjy.im2.client.handler.QuitGroupResponseHandler;
import com.wjy.im2.client.handler.SendToGroupResponseHandler;
import com.wjy.im2.client.handler.SendToUserResponseHandler;
import com.wjy.im2.coder.PacketDecoder;
import com.wjy.im2.coder.PacketEncoder;
import com.wjy.im2.session.SessionUtil;
import com.wjy.util.LogUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7,4));
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new LoginResponseHandler());
                        ch.pipeline().addLast(new SendToUserResponseHandler());
                        ch.pipeline().addLast(new CreateGroupResponseHandler());
                        ch.pipeline().addLast(new GroupMembersResponseHandler());
                        ch.pipeline().addLast(new JoinGroupResponseHandler());
                        ch.pipeline().addLast(new QuitGroupResponseHandler());
                        ch.pipeline().addLast(new SendToGroupResponseHandler());
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        bootstrap.connect("127.0.0.1", 8000).addListener(future -> {
            if(future.isSuccess()) {
                LogUtil.print("连接[127.0.0.1:8000]成功！");
                Channel channel = ((ChannelFuture)future).channel();
                startConsoleThread(channel);
            } else {
                LogUtil.print("连接[127.0.0.1:8000]失败！");
            }
        });
    }

    private static void startConsoleThread(Channel channel) {
        Scanner sc = new Scanner(System.in);
        ConsoleCommandMannager mannager = new ConsoleCommandMannager();
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();

        new Thread(() -> {
            while (!Thread.interrupted()) {
                if(!SessionUtil.hasLogin(channel)) {
                    loginConsoleCommand.exec(sc, channel);
               } else {
                    mannager.exec(sc, channel);
                }
            }
        }).start();
    }
}
