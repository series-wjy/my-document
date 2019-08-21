package com.ts.kafka;

import com.ts.producer.SomeProducerBatch;

import java.io.IOException;

public class ProducerBatchTest {

    public static void main(String[] args) throws IOException {
        SomeProducerBatch producer = new SomeProducerBatch();
        producer.sendMsg();
        System.in.read();
    }
}



