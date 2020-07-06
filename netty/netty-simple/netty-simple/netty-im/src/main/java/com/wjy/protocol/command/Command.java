/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.protocol.command;

/**
 * @author wangjiayou 2019/7/1
 * @version ORAS v1.0
 */
public interface Command {
    Byte COMMON_RESPONSE = 0;

    Byte LOGIN_REQUEST = 1;

    Byte LOGIN_RESPONSE = 2;

    Byte MESSAGE_REQUEST = 3;

    Byte MESSAGE_RESPONSE = 4;

    Byte CREATE_GROUP_REQUEST = 5;

    Byte CREATE_GROUP_RESPONSE = 6;

    Byte SEND_TO_USER_REQUEST = 7;

    Byte SEND_TO_USER_RESPONSE = 8;

    Byte LOGOUT_REQUEST = 9;

    Byte LOGOUT_RESPONSE = 10;

    Byte SEND_TO_GROUP_REQUEST = 11;

    Byte SEND_TO_GROUP_RESPONSE = 12;

    Byte GROUP_MEMEBERS_REQUEST = 13;

    Byte GROUP_MEMEBERS_RESPONSE = 14;

    Byte JOIN_GROUP_REQUEST = 15;

    Byte JOIN_GROUP_RESPONSE = 16;

    Byte QUIT_GROUP_REQUEST = 17;

    Byte QUIT_GROUP_RESPONSE = 18;
}
