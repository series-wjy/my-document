package com.ts.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class TwoProducer {
    // 第一个泛型为key的类型，第二个泛型为消息本身的类型
    private KafkaProducer<Integer, String> producer;

    public TwoProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "");
        properties.put("bootstrap.servers",
                       "kafkaOS1:9092,kafkaOS2:9092,kafkaOS3:9092");
        properties.put("key.serializer",
                          "org.apache.kafka.common.serialization.IntegerSerializer");
        properties.put("value.serializer",
                          "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<Integer, String>(properties);
    }

    public void sendMsg() {
        // 创建记录（消息）
        // 指定主题及消息本身
        // ProducerRecord<Integer, String> record =
        //                        new ProducerRecord<>("cities", "shanghai");
        // 指定主题、key，及消息本身
        // ProducerRecord<Integer, String> record =
        //                        new ProducerRecord<>("cities", 1, "shanghai");
        // 指定主题、要写入的patition、key，及消息本身
        ProducerRecord<Integer, String> record =
                                  new ProducerRecord<>("abc", 0, 1, "shanghai");

        // 可以调用以下两个参数的send()方法，可以在消息发布成功后触发回调的执行
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


















