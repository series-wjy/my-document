/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.server.handler;

import com.wjy.protocol.packet.Packet;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

import static com.wjy.protocol.command.Command.CREATE_GROUP_REQUEST;
import static com.wjy.protocol.command.Command.GROUP_MEMEBERS_REQUEST;
import static com.wjy.protocol.command.Command.JOIN_GROUP_REQUEST;
import static com.wjy.protocol.command.Command.LOGIN_REQUEST;
import static com.wjy.protocol.command.Command.QUIT_GROUP_REQUEST;
import static com.wjy.protocol.command.Command.SEND_TO_GROUP_REQUEST;
import static com.wjy.protocol.command.Command.SEND_TO_USER_REQUEST;

/**
 * @author wangjiayou 2019/7/11
 * @version ORAS v1.0
 */
@ChannelHandler.Sharable
public class IMHandler extends SimpleChannelInboundHandler<Packet> {

    public static final IMHandler INSTANCE = new IMHandler();

    Map<Byte, SimpleChannelInboundHandler<? extends Packet>> map;
    private IMHandler() {
        map = new HashMap<>();
        map.put(CREATE_GROUP_REQUEST, CreateGroupHandler.INSTANCE);
        map.put(SEND_TO_USER_REQUEST, SendToUserHandler.INSTANCE);
        //map.put(LOGOUT_REQUEST,);
        map.put(SEND_TO_GROUP_REQUEST, SendToGroupHandler.INSTANCE);
        map.put(GROUP_MEMEBERS_REQUEST, GroupMemebersHandler.INSTANCE);
        map.put(JOIN_GROUP_REQUEST, JoinGroupHandler.INSTANCE);
        map.put(QUIT_GROUP_REQUEST, QuitGroupHandler.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        map.get(msg.getCommand()).channelRead(ctx, msg);
    }
}
