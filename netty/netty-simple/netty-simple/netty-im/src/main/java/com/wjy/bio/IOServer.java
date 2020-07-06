/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author wangjiayou 2019/6/25
 * @version ORAS v1.0
 */
public class IOServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8000);
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = server.accept();
                    new Thread(() -> {
                        int len = 0;
                        byte[] data = new byte[1024];
                        try {
                            InputStream in = socket.getInputStream();
                            while ((len = in.read(data)) != -1) {
                                System.out.println(new String(data, 0, len));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
