/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.advance;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wangjiayou 2019/6/28
 * @version ORAS v1.0
 */
public class AInboundServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("AInboundServerHandler:" + msg);
        ctx.writeAndFlush(msg);
        super.channelRead(ctx, msg);
    }
}
