package com.ts.kafka;

import com.ts.producer.TwoProducer;

import java.io.IOException;

public class TwoProducerTest {

    public static void main(String[] args) throws IOException {
        TwoProducer producer = new TwoProducer();
        producer.sendMsg();
        System.in.read();
    }
}



