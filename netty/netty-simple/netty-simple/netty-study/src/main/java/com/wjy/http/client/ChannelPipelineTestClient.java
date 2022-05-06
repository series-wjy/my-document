package com.wjy.http.client;

import com.wjy.protocol.packet.codec.PacketCodec;
import com.wjy.protocol.packet.impl.MessageRequestPacket;
import com.wjy.util.LoginUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;

public class ChannelPipelineTestClient {
  public void connect(String host, int port) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group);
      b.channel(NioSocketChannel.class);
      b.option(ChannelOption.SO_KEEPALIVE, true);
      b.handler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) {
        }
      });
      ChannelFuture channelFuture = b.connect("127.0.0.1", 8080).addListener(future -> {
        if (future.isSuccess()) {
          System.out.println(new Date() + "：连接[127.0.0.1:8000]成功！");
          Channel channel = ((ChannelFuture)future).channel();
          startConsoleThread(channel);
        } else {
          System.out.println(new Date() + "：连接[127.0.0.1:8000]失败！");
        }
      });
      channelFuture.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }
  public static void main(String[] args) throws Exception {
    ChannelPipelineTestClient client = new ChannelPipelineTestClient();
    client.connect("127.0.0.1", 8080);
  }

  private static void startConsoleThread(Channel channel) {
    new Thread(() -> {
      while (!Thread.interrupted()) {
        System.out.println("输入消息发送至服务端：");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        MessageRequestPacket messageRequestPacket = new MessageRequestPacket();
        messageRequestPacket.setMsg(line);
        ByteBuf byteBuf = PacketCodec.getInstance().encode(messageRequestPacket);
        channel.writeAndFlush(byteBuf);
      }
    }).start();
  }
}