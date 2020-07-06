/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.advance;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author wangjiayou 2019/6/26
 * @version ORAS v1.0
 */
public class AdvanceNettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossgroup = new NioEventLoopGroup();
        NioEventLoopGroup workgroup = new NioEventLoopGroup();

        serverBootstrap.group(bossgroup, workgroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {

                    }
                });
        bind(serverBootstrap, 1368);
        serverBootstrap.attr(AttributeKey.newInstance("clientName"), "nettyClient");
        //serverBootstrap.option();
    }

    public static void bind(ServerBootstrap serverBootstrap, int port) {
        try {
            serverBootstrap.bind(port).sync().addListener((future) -> {
                if(future.isSuccess()) {
                    System.out.println("端口绑定成功！" + port);
                } else {
                    System.out.println("端口绑定失败！");
                    bind(serverBootstrap, port + 1);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
