package com.ts.kafka;

import com.ts.producer.SimpleProducer;

import java.io.IOException;

public class OneProducerTest {

    public static void main(String[] args) throws IOException {
        SimpleProducer producer = new SimpleProducer();
        producer.sendMsg();
        System.in.read();
    }
}



