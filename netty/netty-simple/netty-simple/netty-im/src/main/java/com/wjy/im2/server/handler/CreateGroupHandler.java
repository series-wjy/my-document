package com.wjy.im2.server.handler;

import com.wjy.im2.session.SessionUtil;
import com.wjy.protocol.packet.impl.CreateGroupRequestPacket;
import com.wjy.protocol.packet.impl.CreateGroupResponsePacket;
import com.wjy.util.IDUtil;
import com.wjy.util.LogUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class CreateGroupHandler extends SimpleChannelInboundHandler<CreateGroupRequestPacket> {
    public static final CreateGroupHandler INSTANCE = new CreateGroupHandler();

    private CreateGroupHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupRequestPacket createGroupRequestPacket) throws Exception {
        List<String> userIds = createGroupRequestPacket.getUserIds();

        List<String> userNames = new ArrayList<>();

        // 1、创建channel组
        ChannelGroup group = new DefaultChannelGroup(ctx.executor());

        // 2、筛选加入群聊的用户userId和userName
        for(String userId : userIds) {
            Channel channel = SessionUtil.getChannel(userId);
            if(channel != null) {
                group.add(channel);
                userNames.add(SessionUtil.getSession(channel).getUserName());
            }
        }

        // 3、创建群聊结果响应
        CreateGroupResponsePacket createGroupResponsePacket = new CreateGroupResponsePacket();
        createGroupResponsePacket.setGroupName(createGroupRequestPacket.getGroupName());
        createGroupResponsePacket.setSuccess(true);
        createGroupResponsePacket.setGroupId(IDUtil.randomId());
        createGroupResponsePacket.setUserNames(userNames);

        SessionUtil.setChannelGroup(createGroupResponsePacket.getGroupId(), group);

        // 4、给群聊的每个成员发送创建成功响应
        group.writeAndFlush(createGroupResponsePacket);

        LogUtil.print("群创建成功[groupId:" + createGroupResponsePacket.getGroupId() + "]群成员包括"
                + createGroupResponsePacket.getUserNames());
    }
}
