package com.ts.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class SomeProducerBatch {
    // 第一个泛型为key的类型，第二个泛型为消息本身的类型
    private KafkaProducer<Integer, String> producer;

    public SomeProducerBatch() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafkaOS1:9092,kafkaOS2:9092,kafkaOS3:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // 指定要批量发送的消息个数，默认16k
        properties.put("batch.size", 16384);  // 16K
        // 指定积攒消息的时长，默认值为0ms
        properties.put("linger.ms", 50);  // 50ms
        this.producer = new KafkaProducer<Integer, String>(properties);
    }

    public void sendMsg() {
        for (int i=0; i<50; i++) {
            ProducerRecord<Integer, String> record =
                    new ProducerRecord<>("cities", 0, i * 10, "city-" + i*100);

            producer.send(record, new Callback() {
                // RecordMetadata，消息元数据，即主题、消息的key、消息本身等的封装对象
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    System.out.print("partition = " + metadata.partition());
                    System.out.print("，topic = " + metadata.topic());
                    System.out.println("，offset = " + metadata.offset());
                }
            });
        }
    }
}

















