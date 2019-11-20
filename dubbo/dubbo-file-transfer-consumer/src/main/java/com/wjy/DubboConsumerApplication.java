package com.wjy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@ImportResource({"classpath*:META-INF/spring/module-*.xml"})
public class DubboConsumerApplication {

	public static void main(String[] args) throws Exception {
        SpringApplication.run(DubboConsumerApplication.class, args);
	}

}
