package com.wjy;

import com.alibaba.dubbo.remoting.http.servlet.DispatcherServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@ComponentScan
@ImportResource({"classpath*:META-INF/spring/module-*.xml"})
public class DubboFileTransferProviderApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DubboFileTransferProviderApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new DispatcherServlet(), "/*");
    }

}

