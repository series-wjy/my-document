package com.wjy.simple.datatrans;

import com.wjy.simple.handler.RequestSampleHandler;
import com.wjy.simple.handler.ResponseSampleEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.net.InetSocketAddress;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年02月21日 16:00:00
 */
public class EchoServer {

    private void writeAndFlushDemo(int port) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap().group(worker, worker);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new FixedLengthFrameDecoder(10))
                            .addLast(new RequestSampleHandler())
                            .addLast(new ResponseSampleEncoder());
                }
            });
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new EchoServer().writeAndFlushDemo(8088);
    }
}
