/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package time;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;

/**
 * @author wangjiayou 2019/9/5
 * @version ORAS v1.0
 */
public class NewDateDemo {
    public static void main(String[] args) {
        localDate();
        localTime();
        localDateTime();
        offsetDateTime();
    }

    private static void offsetDateTime() {
        System.out.println("==============================offsetDateTime====================================");
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        System.out.println(offsetDateTime.toString());              //2013-05-15T10:10:37.257+05:30

        offsetDateTime = OffsetDateTime.now(ZoneId.of("+05:30"));
        System.out.println(offsetDateTime.toString());              //2013-05-15T10:10:37.258+05:30

        offsetDateTime = OffsetDateTime.now(ZoneId.of("-06:30"));
        System.out.println(offsetDateTime.toString());            //2013-05-14T22:10:37.258-06:30

        ZonedDateTime zonedDateTime =
                ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        System.out.println(zonedDateTime.toString());               //2013-05-15T06:45:45.290+02:00[Europe/Paris]
    }

    private static void localDateTime() {
        System.out.println("==============================localDateTime====================================");
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime.toString());      //2013-05-15T10:01:14.911
        System.out.println(localDateTime.getDayOfMonth()); //15
        System.out.println(localDateTime.getHour());       //10
        System.out.println(localDateTime.getNano());       //911000000
    }

    private static void localTime() {
        System.out.println("==============================localTime====================================");
        LocalTime localTime = LocalTime.now();     //toString() in format 09:57:59.744
        //LocalTime localTime = LocalTime.of(12, 20);
        System.out.println(localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString());    //12:20
        System.out.println(localTime.getHour());     //12
        System.out.println(localTime.getMinute());   //20
        System.out.println(localTime.getSecond());   //0
        System.out.println(localTime.MIDNIGHT);      //00:00
        System.out.println(localTime.NOON);          //12:00
    }

    private static void localDate() {
        System.out.println("==============================localDate====================================");
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate.toString());                //2013-05-15
        System.out.println(localDate.getDayOfWeek().toString()); //WEDNESDAY
        System.out.println(localDate.getDayOfMonth());           //15
        System.out.println(localDate.getDayOfYear());            //135
        System.out.println(localDate.isLeapYear());              //false
        System.out.println(localDate.plusDays(27).toString());   //2013-05-27
    }

}
