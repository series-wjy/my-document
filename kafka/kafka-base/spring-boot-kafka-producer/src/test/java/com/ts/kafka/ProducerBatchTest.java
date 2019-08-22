package com.ts.kafka;

import com.ts.producer.ProducerBatch;

import java.io.IOException;

public class ProducerBatchTest {

    public static void main(String[] args) throws IOException {
        ProducerBatch producer = new ProducerBatch();
        producer.sendMsg();
        System.in.read();
    }
}



