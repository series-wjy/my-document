/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.command.impl;

import com.wjy.command.ConsoleCommand;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author wangjiayou 2019/7/9
 * @version ORAS v1.0
 */
public class ConsoleCommandMannager implements ConsoleCommand {

    private Map<String, ConsoleCommand> commandMap;

    public ConsoleCommandMannager() {
        commandMap = new HashMap<>();
        commandMap.put("creategroup", new CreateUserGroupConsoleCommand());
        commandMap.put("sendtouser", new SendToUserConsoleCommand());
        commandMap.put("sendtogroup", new SendToGroupConsoleCommand());
        commandMap.put("joingroup", new JoinGroupConsoleCommand());
        commandMap.put("quitgroup", new QuitGroupConsoleCommand());
        commandMap.put("groupmembers", new GroupMembersConsoleCommand());
        commandMap.put("logout", new LogoutConsoleCommand());
        commandMap.put("login", new LoginConsoleCommand());
    }

    @Override
    public void exec(Scanner scanner, Channel channel) {
        // 获取第一个指令
        String command = scanner.next();
        ConsoleCommand consoleCommand = commandMap.get(command);
        if(consoleCommand != null) {
            consoleCommand.exec(scanner, channel);
        } else {
            System.err.println("无法识别[" + command + "]指令，请重新输入!");
        }
    }
}
