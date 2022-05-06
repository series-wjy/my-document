package com.wjy.simple.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class SampleOutBoundHandler extends ChannelOutboundHandlerAdapter {
    private final String name;
    private final boolean flush;
    private final boolean isException;

    public SampleOutBoundHandler(String name, boolean flush, boolean isException) {
        this.name = name;
        this.flush = flush;
        this.isException = isException;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutBoundHandler: " + name);
        if(isException) {
            throw new RuntimeException("OutBoundHandler:" + name);
        }
        super.write(ctx, msg, promise);
    }


}