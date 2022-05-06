package com.wjy.dependency.di;

import com.wjy.dependency.di.aware.AwaredTestBean;
import com.wjy.dependency.di.configuration.AwareConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 回调注入&延迟注入
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月29日 16:18:00
 */
public class AwareDependencyInjectionDemo {
    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AwareConfiguration.class);
        AwaredTestBean bbb = ctx.getBean(AwaredTestBean.class);
        bbb.printBeanNames();
        System.out.println("-----------");
        System.out.println(bbb.getName());
    }
}
