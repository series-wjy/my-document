/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wangjiayou 2019/6/25
 * @version ORAS v1.0
 */
public class NioServer {
    public static void main(String[] args) throws IOException {
        // 负责轮询是否有新连接
        Selector serverSelector = Selector.open();
        // 负责轮询是否有可读连接
        Selector clientSelector = Selector.open();

        // 接受连接线程
        new Thread(() -> {
            try {
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.socket().bind(new InetSocketAddress(8000));
                ssc.configureBlocking(false);
                ssc.register(serverSelector, SelectionKey.OP_ACCEPT);
                while (true) {
                    // 监测是否有连接，这里的1指的是阻塞时间1ms
                    if(serverSelector.select(1) > 0) {
                        Set<SelectionKey> set = serverSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if(key.isAcceptable()) {
                                try {
                                    // (1)、每来一个连接，不需要创建一个线程，而是直接注册到clientSelector
                                    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                } finally {
                                    keyIterator.remove();
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        // 读取内容线程
        new Thread(() -> {
            while(true) {
                try {
                    // (2)、批量轮询是否有连接有数据可读，这里的1是指阻塞时间1ms
                    if(clientSelector.select(1) > 0) {
                        Set<SelectionKey> set = clientSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if(key.isReadable()) {
                                try {
                                    SocketChannel channel = (SocketChannel) key.channel();
                                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                                    // (3)、面向Buffer
                                    channel.read(buffer);
                                    buffer.flip();
                                    System.out.println(Charset.defaultCharset()
                                            .newDecoder().decode(buffer).toString());
                                } finally {
                                    keyIterator.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
