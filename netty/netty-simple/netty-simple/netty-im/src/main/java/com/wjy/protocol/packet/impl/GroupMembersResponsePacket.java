/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.protocol.packet.impl;

import com.wjy.protocol.command.Command;
import com.wjy.protocol.packet.Packet;
import lombok.Data;

import java.util.List;

/**
 * @author wangjiayou 2019/7/1
 * @version ORAS v1.0
 */
@Data
public class GroupMembersResponsePacket extends Packet {
    private List<String> memebers;

    @Override
    public Byte getCommand() {
        return Command.GROUP_MEMEBERS_RESPONSE;
    }
}
