package com.ts.test;


import com.ts.consumer.SyncAsyncManualConsumer;

public class SyncAsyncManualTest {
    public static void main(String[] args) {
        SyncAsyncManualConsumer consumer = new SyncAsyncManualConsumer();
        consumer.start();
    }
}


