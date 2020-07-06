package com.bjhy.test;


import com.bjhy.collect.common.config.model.CollectTask;
import com.bjhy.collect.common.config.model.type.AgentTypeEnum;
import com.bjhy.collect.common.config.model.type.TaskTypeEnum;
import com.bjhy.collect.extend.annotation.Task;
import com.bjhy.collect.extend.annotation.TaskAgent;

/**
 * @ClassName AgentTaskTest.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年05月13日 15:59:00
 */
@TaskAgent(agentType = AgentTypeEnum.tomcat)
public class AgentTaskTest {

    @Task(taskType = TaskTypeEnum.alarm)
    public void alarm(CollectTask collectTask) {
        System.out.println("被动采集方法，执行一次。。。。。。");
    }

    @Task(taskType = TaskTypeEnum.performance)
    public void performance(CollectTask collectTask) {
        System.out.println("主动采集方法，按频率采集。。。。。。");
    }

    @Task(taskType = TaskTypeEnum.config)
    public void config(CollectTask collectTask) {
        System.out.println("配置数据采集，按频率采集，频率会低一点。。。。。。");
    }
}
