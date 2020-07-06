/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.protocol.packet.codec;

import com.wjy.protocol.packet.Packet;
import com.wjy.protocol.packet.impl.CreateGroupRequestPacket;
import com.wjy.protocol.packet.impl.CreateGroupResponsePacket;
import com.wjy.protocol.packet.impl.GroupMembersRequestPacket;
import com.wjy.protocol.packet.impl.GroupMembersResponsePacket;
import com.wjy.protocol.packet.impl.JoinGroupRequestPacket;
import com.wjy.protocol.packet.impl.JoinGroupResponsePacket;
import com.wjy.protocol.packet.impl.LoginRequestPacket;
import com.wjy.protocol.packet.impl.LoginResponsePacket;
import com.wjy.protocol.packet.impl.LogoutRequestPacket;
import com.wjy.protocol.packet.impl.LogoutResponsePacket;
import com.wjy.protocol.packet.impl.QuitGroupRequestPacket;
import com.wjy.protocol.packet.impl.QuitGroupResponsePacket;
import com.wjy.protocol.packet.impl.SendToGroupRequestPacket;
import com.wjy.protocol.packet.impl.SendToGroupResponsePacket;
import com.wjy.protocol.packet.impl.SendToUserRequestPacket;
import com.wjy.protocol.packet.impl.SendToUserResponsePacket;
import com.wjy.protocol.serialize.Serializer;
import com.wjy.protocol.serialize.impl.JSONSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;

import static com.wjy.protocol.command.Command.CREATE_GROUP_REQUEST;
import static com.wjy.protocol.command.Command.CREATE_GROUP_RESPONSE;
import static com.wjy.protocol.command.Command.GROUP_MEMEBERS_REQUEST;
import static com.wjy.protocol.command.Command.GROUP_MEMEBERS_RESPONSE;
import static com.wjy.protocol.command.Command.JOIN_GROUP_REQUEST;
import static com.wjy.protocol.command.Command.JOIN_GROUP_RESPONSE;
import static com.wjy.protocol.command.Command.LOGIN_REQUEST;
import static com.wjy.protocol.command.Command.LOGIN_RESPONSE;
import static com.wjy.protocol.command.Command.LOGOUT_REQUEST;
import static com.wjy.protocol.command.Command.LOGOUT_RESPONSE;
import static com.wjy.protocol.command.Command.QUIT_GROUP_REQUEST;
import static com.wjy.protocol.command.Command.QUIT_GROUP_RESPONSE;
import static com.wjy.protocol.command.Command.SEND_TO_GROUP_REQUEST;
import static com.wjy.protocol.command.Command.SEND_TO_GROUP_RESPONSE;
import static com.wjy.protocol.command.Command.SEND_TO_USER_REQUEST;
import static com.wjy.protocol.command.Command.SEND_TO_USER_RESPONSE;

/**
 * @author wangjiayou 2019/7/1
 * @version ORAS v1.0
 */
public class PacketCodec {
    private static final int MAGIC_NUMBER = 0x12345678;

    private static final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private static final Map<Byte, Serializer> serializerMap;

    private PacketCodec() {

    }

    public static PacketCodec getInstance() {
        return PacketCodecSingletonHolder.singleton;
    }


    private static class PacketCodecSingletonHolder {
        private static PacketCodec singleton = new PacketCodec();
    }

    static {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(LOGIN_REQUEST, LoginRequestPacket.class);
        packetTypeMap.put(LOGIN_RESPONSE, LoginResponsePacket.class);
        //packetTypeMap.put(MESSAGE_REQUEST, MessageRequestPacket.class);
        //packetTypeMap.put(MESSAGE_RESPONSE, MessageResponsePacket.class);
        packetTypeMap.put(CREATE_GROUP_REQUEST, CreateGroupRequestPacket.class);
        packetTypeMap.put(CREATE_GROUP_RESPONSE, CreateGroupResponsePacket.class);
        packetTypeMap.put(SEND_TO_USER_REQUEST, SendToUserRequestPacket.class);
        packetTypeMap.put(SEND_TO_USER_RESPONSE, SendToUserResponsePacket.class);
        packetTypeMap.put(LOGOUT_REQUEST, LogoutRequestPacket.class);
        packetTypeMap.put(LOGOUT_RESPONSE, LogoutResponsePacket.class);
        packetTypeMap.put(SEND_TO_GROUP_REQUEST, SendToGroupRequestPacket.class);
        packetTypeMap.put(SEND_TO_GROUP_RESPONSE, SendToGroupResponsePacket.class);
        packetTypeMap.put(GROUP_MEMEBERS_REQUEST, GroupMembersRequestPacket.class);
        packetTypeMap.put(GROUP_MEMEBERS_RESPONSE, GroupMembersResponsePacket.class);
        packetTypeMap.put(JOIN_GROUP_REQUEST, JoinGroupRequestPacket.class);
        packetTypeMap.put(JOIN_GROUP_RESPONSE, JoinGroupResponsePacket.class);
        packetTypeMap.put(QUIT_GROUP_REQUEST, QuitGroupRequestPacket.class);
        packetTypeMap.put(QUIT_GROUP_RESPONSE, QuitGroupResponsePacket.class);

        serializerMap = new HashMap<>();
        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializerAlgorithm(), serializer);
    }

    /**
     * 协议包编码
     * @auther wangjiayou
     * @date 2019/7/1
     * @param packet
     * @return io.netty.buffer.ByteBuf
     */
    public ByteBuf encode(Packet packet) {
        // 1、创建ByteBuf对象
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        packetSerialize(packet, byteBuf);

        return byteBuf;
    }

    private void packetSerialize(Packet packet, ByteBuf byteBuf) {
        // 2、序列化协议对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 3、协议拼装
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    /**
     * 协议包编码
     * @auther wangjiayou
     * @date 2019/7/2
     * @param byteBuf
     * @param packet
     * @return io.netty.buffer.ByteBuf
     */

    public ByteBuf encode(ByteBuf byteBuf, Packet packet) {
        // 1、创建ByteBuf对象
        ByteBuf buffer = byteBuf;

        // 2、序列化协议对象
        packetSerialize(packet, buffer);
        return buffer;
    }

    /**
     * 协议包解码
     * @auther wangjiayou
     * @date 2019/7/1
     * @param byteBuf
     * @return com.wjy.protocol.Packet
     */
    public Packet decode(ByteBuf byteBuf) {
        // 跳过magic number
        byteBuf.skipBytes(4);

        // 跳过版本
        byteBuf.skipBytes(1);

        // 序列化算法
        byte serializerAlgorithm = byteBuf.readByte();

        // 指令
        byte command = byteBuf.readByte();

        // 消息内容长度
        int length = byteBuf.readInt();

        // 消息内容
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        // 协议类型
        Class<? extends Packet> requestType = getRequestType(command);
        // 序列化方式
        Serializer serializer = getSerializer(serializerAlgorithm);

        if(requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }
        return null;
    }

    private Serializer getSerializer(byte serializerAlgorithm) {
        return serializerMap.get(serializerAlgorithm);
    }

    private Class<? extends Packet> getRequestType(byte command) {
        return packetTypeMap.get(command);
    }
}
