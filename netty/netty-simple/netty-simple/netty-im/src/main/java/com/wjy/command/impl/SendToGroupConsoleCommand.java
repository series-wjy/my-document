/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.command.impl;

import com.wjy.command.ConsoleCommand;
import com.wjy.protocol.packet.impl.SendToGroupRequestPacket;
import com.wjy.util.LogUtil;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * @author wangjiayou 2019/7/9
 * @version ORAS v1.0
 */
public class SendToGroupConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        SendToGroupRequestPacket sendToGroupRequestPacket = new SendToGroupRequestPacket();
        String groupId = scanner.next();
        String msg = scanner.next();
//        String groupName = SessionUtil.getGroup(groupId).getGroupName();

        LogUtil.print("向群[grouId:" + groupId + "]发送消息");
        sendToGroupRequestPacket.setGroupId(groupId);
        sendToGroupRequestPacket.setMsg(msg);

        channel.writeAndFlush(sendToGroupRequestPacket);
    }
}
