package com.ts;

import com.ts.consumer.AsynManualConsumer;
import com.ts.consumer.SimpleConsumer;
import com.ts.consumer.SyncAsyncManualConsumer;
import com.ts.consumer.SyncManualConsumer;
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
//		SimpleConsumer consumer1 = new SimpleConsumer();
//		consumer1.start();

//		AsynManualConsumer consumer2 = new AsynManualConsumer();
//		consumer2.start();

//		SyncAsyncManualConsumer consumer3 = new SyncAsyncManualConsumer();
//		consumer3.start();

//		SyncManualConsumer consumer4 = new SyncManualConsumer();
//		consumer4.start();
		return null;
	}
}
