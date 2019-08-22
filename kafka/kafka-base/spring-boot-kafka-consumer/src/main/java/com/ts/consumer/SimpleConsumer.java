package com.ts.consumer;

import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

public class SimpleConsumer extends ShutdownableThread {
    private KafkaConsumer<Integer, String> consumer;

    public SimpleConsumer() {
        super("KafkaConsumerTest", false);

        Properties properties = new Properties();
        String brokers = "192.168.56.21:9092,192.168.56.22:9092,192.168.56.23:9092";
        properties.put("bootstrap.servers", brokers);
        // 指定消费者组ID
        properties.put("group.id", "test-group1");
        // 开启offset自动提交
        properties.put("enable.auto.commit", "true");
        // 指定自动提交的最晚时间间隔
        properties.put("auto.commit.interval.ms", "10000");
        // 指定broker认定consumer宕机的时限。从consumer读取消息开始计时，一直到其收到consumer
        // 提交的offset，这个时间段不能超过该值，否则broker认定当前consumer宕机
        properties.put("session.timeout.ms", "30000");
        // 消费者向broker controller发送心跳，即心跳发送频率
        properties.put("heartbeat.interval.ms", "10000");
        // 若没有指定初始的offset或指定的offset不存在，则offset要读取其指定的默认值
        // earliest：从该partition的最开始的offset开始，一般是0
        // lastest：从该partition的最后offset开始，即HW
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer",
                "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        this.consumer = new KafkaConsumer<Integer, String>(properties);
    }

    @Override
    public void doWork() {
        // 指定要消费的主题，可以指定多个主题
        consumer.subscribe(Collections.singletonList("cities"));
        // poll()是阻塞的方法，其参数表示，若broker中没有消息，该poll()等待的最长时间
        // 到时仍没有消息，则返回null
        ConsumerRecords<Integer, String> records = consumer.poll(1000);
        for(ConsumerRecord record : records) {
            System.out.print("topic = " + record.topic());
            System.out.print(" partition = " + record.partition());
            System.out.print(" key = " + record.key());
            System.out.println(" value = " + record.value());
        }
    }
}
