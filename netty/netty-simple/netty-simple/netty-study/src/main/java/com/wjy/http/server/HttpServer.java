package com.wjy.http.server;

import com.wjy.http.handler.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
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
public class HttpServer {
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
                    ch.pipeline().addLast("codec", new HttpServerCodec()) // HTTP 编解码
                            .addLast("compressor", new HttpContentCompressor())// HttpContent 压缩
                            .addLast("aggregator", new HttpObjectAggregator(65536))// HTTP 消息聚合
                            .addLast("handler", new HttpServerHandler());// 自定义业务逻辑处理器
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
        new HttpServer().start(8080);
    }
}
