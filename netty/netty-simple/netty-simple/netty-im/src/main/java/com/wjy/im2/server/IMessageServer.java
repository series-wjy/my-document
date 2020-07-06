/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.server;

import com.wjy.im2.server.handler.*;
import com.wjy.util.LogUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author wangjiayou 2019/7/2
 * @version ORAS v1.0
 */
public class IMessageServer {

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

                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7,4));
                        //ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                        ch.pipeline().addLast(LoginRequestHandler.INSTANCE);

                        // 用户登录验证
                        ch.pipeline().addLast(new AuthHandler());
                        // 单例模式，客户端共享handler
                        ch.pipeline().addLast(IMHandler.INSTANCE);
                        ch.pipeline().addLast(new TestOutBoundHandler());
                        ch.pipeline().addLast(new TestOutBoundHandler2());
                        ch.pipeline().addLast(new LifeCyCleTestHandler());
                        //ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        serverBootstrap.bind(8000).addListener(future -> {
            if(future.isSuccess()) {
                LogUtil.print("绑定端口[8000]成功！");
                //bossGroup.scheduleAtFixedRate();
            } else {
                LogUtil.print("绑定端口[8000]失败！");
            }
        });
    }
}
