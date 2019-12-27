package com.bjhy.collect.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;


/**
 * @ClassName KafkaClientTemplate.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月26日 14:17:00
 */
public class KafkaClientTemplate {

    private static final KafkaProducer producer = ProducerHolder.getInstance();

    private KafkaClientTemplate() {

    }

    private static class ProducerHolder {
        private static KafkaProducer getInstance() {
            Properties props = new Properties();
            props.put("bootstrap.servers", "192.168.0.213:9092");
            props.put("acks", "all");
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            return new KafkaProducer<>(props);
        }
    }

    public static void sendToKafka(String topic, String msg) {
        producer.send(new ProducerRecord<String, String>(topic, msg));
    }
}
