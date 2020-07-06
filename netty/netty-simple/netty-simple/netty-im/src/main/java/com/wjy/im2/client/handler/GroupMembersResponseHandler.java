/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.client.handler;

import com.wjy.protocol.packet.impl.GroupMembersResponsePacket;
import com.wjy.util.LogUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wangjiayou 2019/7/11
 * @version ORAS v1.0
 */
public class GroupMembersResponseHandler extends SimpleChannelInboundHandler<GroupMembersResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersResponsePacket msg) throws Exception {
        LogUtil.print("群成员列表" + msg.getMemebers());
    }
}
