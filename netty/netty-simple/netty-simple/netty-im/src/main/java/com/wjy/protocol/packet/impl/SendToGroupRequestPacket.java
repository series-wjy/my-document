/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.protocol.packet.impl;

import com.wjy.protocol.command.Command;
import com.wjy.protocol.packet.Packet;
import lombok.Data;

/**
 * @author wangjiayou 2019/7/1
 * @version ORAS v1.0
 */
@Data
public class SendToGroupRequestPacket extends Packet {
    private String groupId;

    private String fromUserId;

    private String fromUserName;

    private String msg;

    @Override
    public Byte getCommand() {
        return Command.SEND_TO_GROUP_REQUEST;
    }
}
