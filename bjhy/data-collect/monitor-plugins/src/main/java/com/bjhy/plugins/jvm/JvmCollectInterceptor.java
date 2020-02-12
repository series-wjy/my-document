package com.bjhy.plugins.jvm;
import com.bjhy.core.kafka.KafkaClientTemplate;
import com.bjhy.core.plugin.api.MonitorInterceptor;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName JvmDataCollectAgentInterceptor.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月25日 16:25:00
 */
public class JvmCollectInterceptor implements MonitorInterceptor<JvmInfoCollect.TransportJvm> {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);


    @Override
    public JvmInfoCollect.TransportJvm beforeMethod(Method method, Object[] allArguments) throws Throwable {
        return null;
    }

    @Override
    public Object afterMethod(Method method, Object[] allArguments, Object ret, JvmInfoCollect.TransportJvm span) throws Throwable {
        executor.scheduleAtFixedRate(() -> {
            System.out.println("==================执行采集任务==============================");
            String msg = JvmInfoCollect.collect();
            KafkaClientTemplate.sendToKafka("jvm_info_topic", msg);
        }, 1, 1, TimeUnit.MINUTES);
        return null;
    }

    @Override
    public void handleMethodException(Method method, Object[] allArguments, Throwable throwable, JvmInfoCollect.TransportJvm span) {

    }
}
