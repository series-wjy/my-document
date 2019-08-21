package com.ts.test;


import com.ts.consumer.SyncManualConsumer;

public class SyncManualTest {
    public static void main(String[] args) {
        SyncManualConsumer consumer = new SyncManualConsumer();
        consumer.start();
    }
}


