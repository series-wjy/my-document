/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.command.impl;

import com.wjy.command.ConsoleCommand;
import com.wjy.protocol.packet.impl.QuitGroupRequestPacket;
import com.wjy.util.LogUtil;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * @author wangjiayou 2019/7/9
 * @version ORAS v1.0
 */
public class QuitGroupConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        QuitGroupRequestPacket quitGroupRequestPacket = new QuitGroupRequestPacket();
        LogUtil.print("退出群聊");
        String groupId = scanner.next();
        quitGroupRequestPacket.setGroupId(groupId);

        channel.writeAndFlush(quitGroupRequestPacket);

        waitForLoginResponse();
    }
}
