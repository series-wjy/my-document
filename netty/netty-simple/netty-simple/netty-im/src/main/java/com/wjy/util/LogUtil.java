package com.wjy.util;

import java.time.LocalDateTime;

public class LogUtil {

    public static void print(String log) {
        LocalDateTime now = LocalDateTime.now();
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("");
        System.out.println(now + ":" + log);
    }
}
