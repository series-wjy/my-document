package com.bjhy.collect.jvm;

import com.bjhy.collect.kafka.KafkaClientTemplate;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * @ClassName JvmDataCollectAgentInterceptor.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月25日 16:25:00
 */
public class JvmCollectAgentInterceptor1 {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @RuntimeType
    public Object intercept(@This Object obj,
                            @AllArguments Object[] allArguments,
                            @SuperCall Callable<?> zuper,
                            @Origin Method method) throws Throwable {
        try {
            // 原方法执行前
            System.out.println(" before method:" + method.getName());
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("class[" + obj.getClass() + "] before method[" + method.getName() + "] intercept failure");
        }

        Object ret = null;
        try {
            // 原方法调用
            ret = zuper.call();
        } catch (Throwable t) {
            try {
                // 原方法调用异常
                System.out.println("exception method:" + method.getName());
            } catch (Throwable t2) {
                System.out.println("class[" + obj.getClass() + "] handle method[" + method.getName() + "] exception failure");
            }
            throw t;
        } finally {
            try {
                // 原方法执行后
                System.out.println("after method:" + method.getName());
                executor.scheduleAtFixedRate(() -> {
                    System.out.println("==================执行采集任务==============================");
                    String msg = JvmInfoCollect.collect();
                    KafkaClientTemplate.sendToKafka("jvm_info_topic", msg);
                }, 1, 1, TimeUnit.MINUTES);
            } catch (Throwable t) {
                System.out.println("class[" + obj.getClass() + "] after method[" + method.getName() + "] intercept failure");
            }
        }
        return ret;
    }
}
