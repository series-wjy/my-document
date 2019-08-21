package com.ts;

import com.ts.consumer.SimpleConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KafkaConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaConsumerApplication.class, args);
	}

	@Bean
	public SimpleConsumer simpleConsumer() {
		SimpleConsumer consumer = new SimpleConsumer();
		consumer.start();
		return consumer;
	}
}
