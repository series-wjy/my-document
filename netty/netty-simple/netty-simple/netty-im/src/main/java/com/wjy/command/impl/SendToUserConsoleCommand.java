/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.command.impl;

import com.wjy.command.ConsoleCommand;
import com.wjy.protocol.packet.impl.SendToUserRequestPacket;
import com.wjy.util.LogUtil;
import io.netty.channel.Channel;

import java.util.Date;
import java.util.Scanner;

/**
 * @author wangjiayou 2019/7/9
 * @version ORAS v1.0
 */
public class SendToUserConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        SendToUserRequestPacket sendToUserRequestPacket = new SendToUserRequestPacket();
        String toUserId = scanner.next();
        String msg = scanner.next();

        LogUtil.print("向用户[toUserId:" + toUserId + "]发送消息");
        sendToUserRequestPacket.setMsg(msg);
        sendToUserRequestPacket.setToUserId(toUserId);
        channel.writeAndFlush(sendToUserRequestPacket);
    }
}
