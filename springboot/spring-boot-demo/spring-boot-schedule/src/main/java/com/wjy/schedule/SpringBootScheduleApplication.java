package com.wjy.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootScheduleApplication.class, args);
	}

}
