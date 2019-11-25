package com.example.demo;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName DateTimeTest.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年11月25日 11:38:00
 */
public class DateTimeTest {

    @Test
    public void test() {

        String str = "2019-11-21 20:33:41";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        LocalDateTime ordinary = LocalDateTime.parse(str, formatter);
        Instant instant = ordinary.toInstant(ZoneOffset.ofHours(8));
        System.out.println(ordinary.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
        System.out.println(ZonedDateTime.of(ordinary, ZoneId.systemDefault()).format(formatter2));

        CharSequence cs = "abc";
        String abc = "abc";

        System.out.println(cs == abc);
        System.out.println(cs.toString() == abc);
        System.out.println(cs.equals(abc));
    }
}
