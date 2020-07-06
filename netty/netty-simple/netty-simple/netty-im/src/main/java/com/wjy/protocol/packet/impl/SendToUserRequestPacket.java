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
public class SendToUserRequestPacket extends Packet {
    private String toUserId;

    private String msg;

    @Override
    public Byte getCommand() {
        return Command.SEND_TO_USER_REQUEST;
    }
}
