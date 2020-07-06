/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.advance;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author wangjiayou 2019/6/27
 * @version ORAS v1.0
 */
public class AdvanceNettyClient {
    private static final int MAX_RETRY = 3;

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                    }
                });
        connect(bootstrap, "127.0.0.1", 8000, 3);
    }

    public static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect("127.0.0.1", 8000).addListener((future)-> {
            if (future.isSuccess()) {
                System.out.println("连接成功!");
            } else if (retry == 0) {
                System.err.println("连接失败， 不再重试!");
            } else {
                // 重连次数
                int order = (MAX_RETRY - retry) + 1;
                // 重连间隔
                int interval = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() ->connect(bootstrap, host, port, retry - 1),
                        interval, TimeUnit.SECONDS);
            }
        });
    }
}
