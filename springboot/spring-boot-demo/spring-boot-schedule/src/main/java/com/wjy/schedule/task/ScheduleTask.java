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

    @Scheduled(cron="*/6 * * * * ?")
    public void process() {
        System.out.println("this is scheduled task" + count);
    }
}
