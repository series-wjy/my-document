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
public class LogoutRequestPacket extends Packet {
    private String userId;

    private boolean offline;

    @Override
    public Byte getCommand() {
        return Command.LOGOUT_REQUEST;
    }
}
