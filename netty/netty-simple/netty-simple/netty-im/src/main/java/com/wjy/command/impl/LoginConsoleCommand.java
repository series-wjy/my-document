/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.command.impl;

import com.wjy.command.ConsoleCommand;
import com.wjy.protocol.packet.impl.LoginRequestPacket;
import com.wjy.util.LogUtil;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * @author wangjiayou 2019/7/9
 * @version ORAS v1.0
 */
public class LoginConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        LogUtil.print("用户登录");
        String userName = scanner.next();
        loginRequestPacket.setUsername(userName);
        loginRequestPacket.setPassword("pwd");

        channel.writeAndFlush(loginRequestPacket);

        waitForLoginResponse();
    }
}
