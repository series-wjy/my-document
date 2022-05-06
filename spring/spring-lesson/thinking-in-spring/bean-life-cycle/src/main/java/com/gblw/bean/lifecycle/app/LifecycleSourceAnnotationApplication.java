package com.gblw.bean.lifecycle.app;

import com.gblw.bean.lifecycle.configuration.LifecycleSourceConfiguration;
import com.gblw.bean.lifecycle.domain.Cat;
import com.gblw.bean.lifecycle.domain.Person;
import com.gblw.bean.lifecycle.processor.LifecycleDestructionPostProcessor;
import com.gblw.bean.lifecycle.processor.LifecycleNameReadPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class LifecycleSourceAnnotationApplication {
    
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(LifecycleSourceConfiguration.class);
        ctx.register(LifecycleNameReadPostProcessor.class);
        ctx.register(LifecycleDestructionPostProcessor.class);

        System.out.println("================准备刷新IOC容器==================");

        ctx.refresh();

        System.out.println("================IOC容器刷新完毕==================");

        ctx.start();

        System.out.println("================IOC容器启动完成==================");

        Person person = ctx.getBean(Person.class);
        System.out.println(person);
        Cat cat = ctx.getBean(Cat.class);
        System.out.println(cat);

        System.out.println("================准备停止IOC容器==================");

        ctx.stop();

        System.out.println("================IOC容器停止成功==================");

        ctx.close();
    }
}