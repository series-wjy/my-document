package com.wjy.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudProvider2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudProvider2Application.class, args);
	}

}
