/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.schedule.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.SimpleTimeZone;

/**
 * @author wangjiayou 2019/8/5
 * @version ORAS v1.0
 */
@Component
public class ScheduleTask2 {

    private static final LocalTime time = LocalTime.now();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");

    @Scheduled(fixedRate = 6000)
    public void process() {
        System.out.println("current time:" + formatter.format(LocalTime.now()));
    }
}
