/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.schedule.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wangjiayou 2019/8/5
 * @version ORAS v1.0
 */
@Component
public class ScheduleTask {

    private int count = 0;
    byte[] bytes;
    byte[] bytes1;

    @Scheduled(cron="*/5 * * * * ?")
    public void process() {
        bytes = new byte[1024*1024*10];
        bytes1 = new byte[1024*1024*1];
        System.out.println("this is scheduled task" + count);
    }
}
