/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.advance;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wangjiayou 2019/6/28
 * @version ORAS v1.0
 */
public class IMNettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new AInboundServerHandler());
                    ch.pipeline().addLast(new BInboundServerHandler());
                    ch.pipeline().addLast(new CInboundServerHandler());
                    ch.pipeline().addLast(new AOutboundServerHandler());
                    ch.pipeline().addLast(new BOutboundServerHandler());
                    ch.pipeline().addLast(new COutboundServerHandler());
                }
            });
        serverBootstrap.bind(8000).addListener(future -> {
            System.out.println("服务端绑定端口【8000】成功！");
        });
    }
}
