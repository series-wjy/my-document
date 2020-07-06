/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.unpack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author wangjiayou 2019/6/28
 * @version ORAS v1.0
 */
public class UnpackServer {
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
                    //ch.pipeline().addLast(new FixedLengthFrameDecoder(50));
                    //ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,
                            Unpooled.copiedBuffer("$".getBytes("utf-8"))));
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7,4));
                    //ch.pipeline().addLast(new FixedLengthFrameDecoder(50));
                    //ch.pipeline().addLast(new FixedLengthFrameDecoder(50));
                    ch.pipeline().addLast(new UnpackServerHandler());
                }
            });
        serverBootstrap.bind(8000).addListener(future -> {
            System.out.println("服务端绑定端口【8000】成功！");
        });
    }
}
