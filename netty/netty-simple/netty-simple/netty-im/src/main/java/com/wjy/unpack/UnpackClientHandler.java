/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.unpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author wangjiayou 2019/6/28
 * @version ORAS v1.0
 */
public class UnpackClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + ":客户端写出数据");
        for(int i = 0; i < 100; i ++) {
            // 1.获取数据
            ByteBuf byteBuf = getByteBuf(ctx);
            //byteBuf.writeBytes(System.getProperty("line.separator").getBytes("utf-8"));
            byteBuf.writeBytes("$".getBytes("utf-8"));

            // 2.写数据
            ctx.writeAndFlush(byteBuf);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + ": 客户端收到消息->" + byteBuf.toString(Charset.forName("utf-8")));
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        // 1.获取二进制抽象 ByteBuf
        ByteBuf buff = ctx.alloc().buffer();
        // 2.准备数据
        byte[] bytes = "你好，微信好友！感谢你关注netty学习公众号跟着大家一起学习进步！".getBytes(Charset.forName("utf-8"));
        // 3.填充数据到 ByteBuf
        buff.writeBytes(bytes);
        return buff;
    }
}
