package com.bjhy.common.kafka;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * @ClassName KafkaClientTemplate.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年2月10日 14:17:00
 */
public class KafkaClientTemplate {

    private static final KafkaProducer producer = ProducerHolder.getInstance();

    private KafkaClientTemplate() {

    }

    private static class ProducerHolder {
        private static KafkaProducer getInstance() {
            Properties props = new Properties();
            props.put("bootstrap.servers", "192.168.0.215:9093");
            props.put("acks", "all");
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            return new KafkaProducer<>(props);
        }
    }

    public static void sendToKafka(String topic, String msg) {
        producer.send(new ProducerRecord<String, String>(topic, msg));
    }

    public static void main(String[] args) {
        KafkaClientTemplate.sendToKafka("test-topic","abc");
    }
}
