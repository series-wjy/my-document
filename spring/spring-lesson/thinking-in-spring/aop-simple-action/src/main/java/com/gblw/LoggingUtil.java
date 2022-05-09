package com.gblw;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingUtil {
    public static void beforeMethod(String method) {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " 开始执行 " + method + " 方法");
    }

    public static void afterMethod(String method) {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " " + method + " 方法执行完成");
    }
}
