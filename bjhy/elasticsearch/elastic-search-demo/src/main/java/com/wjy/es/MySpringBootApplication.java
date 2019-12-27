package com.wjy.es;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@MapperScan("com.wjy.es")
@EnableTransactionManagement
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MySpringBootApplication {

	private final static Logger logger = LoggerFactory.getLogger(MySpringBootApplication.class);

    public static void main(String[] args) { 
    	
    	new Thread(()->{
			for (int i=0;i<100;i++){
				logger.info("---test---"+i);
			}
		}).start();
    	
        //入口运行类
        SpringApplication.run(MySpringBootApplication.class, args);
        
    }

}