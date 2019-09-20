/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package feature8.time;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAdjusters;
import java.time.zone.ZoneRules;

/**
 * @author wangjiayou 2019/9/5
 * @version ORAS v1.0
 */
public class TimestampAndDuration {
    public static void main(String[] args) {
        instant();
        duration();
        period();
        utility();
    }

    private static void utility() {
        System.out.println("==============================utility1====================================");
        //day-of-week to represent, from 1 (Monday) to 7 (Sunday)
        System.out.println(DayOfWeek.of(2));                    //TUESDAY

        DayOfWeek day = DayOfWeek.TUESDAY;
        System.out.println(day.getValue());                     //5

        LocalDate localDate = LocalDate.now();
        System.out.println(localDate.with(DayOfWeek.MONDAY));  //2013-05-13  i.e. when was monday in current week ?

        LocalDate date = LocalDate.of(2013, Month.FEBRUARY, 15);                     //Today
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println(endOfMonth.toString());                              //2013-05-31

        LocalDate nextTue = date.with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        System.out.println(nextTue.toString());

        System.out.println("==============================utility2====================================");
        //Builder pattern used to make date object
        OffsetDateTime date1 = Year.of(2013)
                .atMonth(Month.MAY).atDay(15)
                .atTime(0, 0)
                .atOffset(ZoneOffset.of("+03:00"));
        System.out.println(date1);                                     //2013-05-15T00:00+03:00

        //factory method used to make date object
        OffsetDateTime date2 = OffsetDateTime.
                of(2013, 5, 15, 0, 0, 0, 0, ZoneOffset.of("+03:00"));
        System.out.println(date2);

        System.out.println("==============================utility3====================================");
        Clock clock = Clock.systemDefaultZone();
        System.out.println(clock);                      //SystemClock[Asia/Calcutta]
        System.out.println(clock.instant().toString()); //2013-05-15T06:36:33.837Z
        System.out.println(clock.getZone());            //Asia/Calcutta

        Clock anotherClock = Clock.system(ZoneId.of("Europe/Tiraspol"));
        System.out.println(anotherClock);                       //SystemClock[Europe/Tiraspol]
        System.out.println(anotherClock.instant().toString());  //2013-05-15T06:36:33.857Z
        System.out.println(anotherClock.getZone());             //Europe/Tiraspol

        Clock forwardedClock  = Clock.tick(anotherClock, Duration.ofSeconds(60));
        System.out.println(forwardedClock.instant().toString());  //2013-05-15T06:30Z

        System.out.println("==============================utility4====================================");
        //Zone rules
        System.out.println(ZoneRules.of(ZoneOffset.of("+08:00")).isDaylightSavings(Instant.now()));
        System.out.println(ZoneRules.of(ZoneOffset.of("+08:00")).isFixedOffset());

        System.out.println("==============================utility5====================================");
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.append(DateTimeFormatter.BASIC_ISO_DATE)
                .appendLiteral("")
                .appendZoneOrOffsetId();
        DateTimeFormatter formatter = formatterBuilder.toFormatter();
        System.out.println(formatter.format(ZonedDateTime.now()));
    }

    private static void instant() {
        System.out.println("==============================instant====================================");
        Instant instant = Clock.system(ZoneId.of("Asia/Tokyo")).instant();
        System.out.println(Clock.systemDefaultZone());
        System.out.println(instant.plus(Duration.ofHours(8)).toString());                                 //2013-05-15T05:20:08.145Z
        System.out.println(instant.plus(Duration.ofMillis(5000)).toString());   //2013-05-15T05:20:13.145Z
        System.out.println(instant.minus(Duration.ofMillis(5000)).toString());  //2013-05-15T05:20:03.145Z
        System.out.println(instant.minusSeconds(10).toString());                //2013-05-15T05:19:58.145Z
    }

    private static void duration() {
        System.out.println("==============================duration====================================");
        Duration duration = Duration.ofMillis(5000);
        System.out.println(duration.toString());     //PT5S
        duration = Duration.ofSeconds(60);
        System.out.println(duration.toString());     //PT1M
        duration = Duration.ofMinutes(10);
        System.out.println(duration.toString());     //PT10M
        duration = Duration.ofHours(2);
        System.out.println(duration.toString());     //PT2H
        duration = Duration.between(Instant.now(), Instant.now().plus(Duration.ofMinutes(10)));
        System.out.println(duration.toString());  //PT10M
        System.out.println(duration.toDays());
    }

    private static void period() {
        System.out.println("==============================period====================================");
        Period period = Period.ofDays(6);
        System.out.println(period.toString());    //P6D

        period = Period.ofMonths(6);
        System.out.println(period.toString());    //P6M

        period = Period.between(LocalDate.now(),
                LocalDate.now().plusDays(370));
        System.out.println(period.toString());   //P1M29D
    }
}
