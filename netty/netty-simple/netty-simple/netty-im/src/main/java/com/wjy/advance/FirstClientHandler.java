/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.advance;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author wangjiayou 2019/6/28
 * @version ORAS v1.0
 */
public class FirstClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + ":客户端写出数据");
        // 1.获取数据
        ByteBuf byteBuf = getByteBuf(ctx);

        System.out.println("============== byteBuf属性：=====================");
        System.out.println("capacity：" + byteBuf.capacity());
        System.out.println("maxCapacity：" + byteBuf.maxCapacity());
        System.out.println("readableBytes：" + byteBuf.readableBytes());
        System.out.println("isReadable：" + byteBuf.isReadable());
        System.out.println("readerIndex：" + byteBuf.readerIndex());
        System.out.println("writableBytes：" + byteBuf.writableBytes());
        System.out.println("isWritable：" + byteBuf.isWritable());
        System.out.println("writerIndex：" + byteBuf.writerIndex());
        System.out.println("maxWritableBytes：" + byteBuf.maxWritableBytes());
        byteBuf.markReaderIndex();

        //byteBuf.readerIndex(24);
        //byteBuf.readByte();
        //byteBuf.writerIndex(100);

        ByteBuf slice = byteBuf.slice();
       //System.out.println("====>> slice：" + slice.readBytes(24).toString(Charset.forName("utf-8")));
        System.out.println("====>> readerIndex：" + slice.readerIndex());
        System.out.println("====>> writerIndex：" + slice.writerIndex());
        System.out.println("====>> capacity：" + slice.capacity());
        System.out.println("====>> maxCapacity：" + slice.maxCapacity());

        // 2.写数据
        ctx.channel().writeAndFlush(byteBuf);

        //byteBuf.resetReaderIndex();
        System.out.println("readerIndex：" + byteBuf.readerIndex());
        System.out.println("writerIndex：" + byteBuf.writerIndex());
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
        byte[] bytes = "你好，微信好友！你好，微信好友！".getBytes(Charset.forName("utf-8"));
        // 3.填充数据到 ByteBuf
        buff.writeBytes(bytes);
        return buff;
    }
}
