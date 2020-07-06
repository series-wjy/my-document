/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.command.impl;

import com.wjy.command.ConsoleCommand;
import com.wjy.im2.session.SessionUtil;
import com.wjy.protocol.packet.impl.LogoutRequestPacket;
import io.netty.channel.Channel;

import java.util.Date;
import java.util.Scanner;

/**
 * @author wangjiayou 2019/7/9
 * @version ORAS v1.0
 */
public class LogoutConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        LogoutRequestPacket logoutRequestPacket = new LogoutRequestPacket();
        String userId = SessionUtil.getSession(channel).getUserId();
        String userName = SessionUtil.getSession(channel).getUserName();

        System.out.println(new Date() + ": 用户下线操作[userId:" + userId + " userName:" + userName + "]");
        logoutRequestPacket.setOffline(true);
        logoutRequestPacket.setUserId(userId);
        channel.writeAndFlush(logoutRequestPacket);
    }
}
