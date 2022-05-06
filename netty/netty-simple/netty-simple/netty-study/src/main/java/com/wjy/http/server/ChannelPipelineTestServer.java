package com.wjy.http.server;

import com.wjy.http.handler.HttpServerHandler;
import com.wjy.simple.handler.ExceptionHandler;
import com.wjy.simple.handler.SampleInBoundHandler;
import com.wjy.simple.handler.SampleOutBoundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.net.InetSocketAddress;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年02月15日 15:20:00
 */
public class ChannelPipelineTestServer {
    public void start(int port) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap().group(boss, worker);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.localAddress(new InetSocketAddress(port));
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new SampleInBoundHandler("SampleInBoundHandlerA", false, false))
                            .addLast(new SampleInBoundHandler("SampleInBoundHandlerB", false, false))
                            .addLast(new SampleInBoundHandler("SampleInBoundHandlerC", true, false));
                    ch.pipeline()
                            .addLast(new SampleOutBoundHandler("SampleOutBoundHandlerA", false, false))
                            .addLast(new SampleOutBoundHandler("SampleOutBoundHandlerB", false, true))
                            .addLast(new SampleOutBoundHandler("SampleOutBoundHandlerC", false, false));
                    ch.pipeline().addLast(new ExceptionHandler());
                }
            });
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture sync = bootstrap.bind().sync();
            System.out.println("Http Server started, Listening on " + port);
            sync.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ChannelPipelineTestServer().start(8080);
    }
}
