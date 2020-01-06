package com.hyxt.collect;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName JavaTimeTest.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年01月06日 14:29:00
 */
public class JavaTimeTest {
    @Test
    public void test6() throws InterruptedException {
        String str1 = "2020-01-06 02:25:19";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(str1, dtf);

        LocalDateTime callTime = LocalDateTime.parse(str1, dtf2);
        System.out.println(parse);

        LocalDateTime time1 = LocalDateTime.now();
        TimeUnit.SECONDS.sleep(1);
        LocalDateTime time2 = LocalDateTime.now();
        System.out.println(Duration.between(time1, time2).toMillis());
    }
}
