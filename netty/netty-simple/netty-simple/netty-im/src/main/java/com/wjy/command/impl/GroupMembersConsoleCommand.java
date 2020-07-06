/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.command.impl;

import com.wjy.command.ConsoleCommand;
import com.wjy.protocol.packet.impl.GroupMembersRequestPacket;
import com.wjy.util.LogUtil;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * @author wangjiayou 2019/7/9
 * @version ORAS v1.0
 */
public class GroupMembersConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        GroupMembersRequestPacket groupMemebersRequestPacket = new GroupMembersRequestPacket();
        LogUtil.print("获取群成员列表");
        String groupId = scanner.next();
        groupMemebersRequestPacket.setGroupId(groupId);

        channel.writeAndFlush(groupMemebersRequestPacket);

        waitForLoginResponse();
    }
}
