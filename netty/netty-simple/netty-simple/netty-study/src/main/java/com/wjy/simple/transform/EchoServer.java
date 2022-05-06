package com.wjy.simple.transform;

import com.wjy.simple.handler.ExceptionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * 演示 netty 数据传输流程
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年12月17日 13:39:00
 */
public class EchoServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
                        ch.pipeline().addLast(new RequestSampleHandler());
                        ch.pipeline().addLast(new ResponseSampleEncoder());
                        ch.pipeline().addLast(new ExceptionHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind(8088).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 返回消息编码
     */
    private static class ResponseSampleEncoder extends MessageToByteEncoder<ResponseSample> {

        @Override
        protected void encode(ChannelHandlerContext ctx, ResponseSample msg, ByteBuf out) throws Exception {
            if(msg != null) {
                out.writeBytes(msg.getStatus().getBytes());
                out.writeBytes(msg.getData().getBytes());
                out.writeLong(msg.getTimeStamp());
            }
        }
    }

    /**
     * 读取消息并封装
     */
    private static class RequestSampleHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf data = (ByteBuf) msg;
            ResponseSample result = new ResponseSample("OK",
                    data.toString(Charset.defaultCharset()), System.currentTimeMillis());
            ctx.writeAndFlush(result);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println(cause);
        }
    }


    private static class ResponseSample {
        private String status;
        private String data;
        private long timeStamp;

        public ResponseSample(String status, String data, long timeStamp) {
            this.status = status;
            this.data = data;
            this.timeStamp = timeStamp;
        }

        public String getStatus() {
            return status;
        }

        public String getData() {
            return data;
        }

        public long getTimeStamp() {
            return timeStamp;
        }
    }
}
