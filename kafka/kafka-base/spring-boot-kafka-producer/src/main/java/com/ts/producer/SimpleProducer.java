package com.ts.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class SimpleProducer {
    // 第一个泛型为key的类型，第二个泛型为消息本身的类型
    private KafkaProducer<Integer, String> producer;

    public SimpleProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafkaOS1:9092,kafkaOS2:9092,kafkaOS3:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

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
                                  new ProducerRecord<>("cities", 1, 1, "shanghai");

        // 发布消息，其返回值为Future对象，表示其发送过程为异步，不过这里不使用该返回结果
        // Future<RecordMetadata> future = producer.send(record);
        producer.send(record);
    }
}

















