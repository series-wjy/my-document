package com.wjy.im2.server.handler;

import com.wjy.im2.session.Session;
import com.wjy.im2.session.SessionUtil;
import com.wjy.protocol.packet.impl.GroupMembersRequestPacket;
import com.wjy.protocol.packet.impl.GroupMembersResponsePacket;
import com.wjy.util.LogUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class GroupMemebersHandler extends SimpleChannelInboundHandler<GroupMembersRequestPacket> {
    public static final GroupMemebersHandler INSTANCE = new GroupMemebersHandler();

    private GroupMemebersHandler() {

    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestPacket groupMemebersRequestPacket) throws Exception {
        String groupId = groupMemebersRequestPacket.getGroupId();

        ChannelGroup group = SessionUtil.getChannelGroup(groupId);
        List<String> memebers = new ArrayList<>();
        if(group != null) {
            group.forEach(channel -> {
                Session session = SessionUtil.getSession(channel);
                memebers.add(session.getUserId() + ":" + session.getUserName());
            });
        }
        GroupMembersResponsePacket response = new GroupMembersResponsePacket();
        response.setMemebers(memebers);

        ctx.channel().writeAndFlush(response);
        LogUtil.print("获取群成员列表[groupId:" + groupMemebersRequestPacket.getGroupId() + "]群成员包括" + memebers);
    }
}
