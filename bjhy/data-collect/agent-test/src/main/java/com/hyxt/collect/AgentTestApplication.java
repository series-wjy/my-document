package com.hyxt.collect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class AgentTestApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(AgentTestApplication.class, args);
        //System.in.read();
    }

}
