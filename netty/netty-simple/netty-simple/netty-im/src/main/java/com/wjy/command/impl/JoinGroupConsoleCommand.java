/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.command.impl;

import com.wjy.command.ConsoleCommand;
import com.wjy.protocol.packet.impl.JoinGroupRequestPacket;
import com.wjy.util.LogUtil;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * @author wangjiayou 2019/7/9
 * @version ORAS v1.0
 */
public class JoinGroupConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        JoinGroupRequestPacket joinGroupRequestPacket = new JoinGroupRequestPacket();
        LogUtil.print("加入群聊");
        String groupId = scanner.next();
        joinGroupRequestPacket.setGroupId(groupId);

        channel.writeAndFlush(joinGroupRequestPacket);

        waitForLoginResponse();
    }
}
