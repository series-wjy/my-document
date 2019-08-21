package com.ts.test;

import com.ts.consumer.AsynManualConsumer;

public class AsyncManualTest {

    public static void main(String[] args) {
        AsynManualConsumer consumer = new AsynManualConsumer();
        consumer.start();
    }
}


