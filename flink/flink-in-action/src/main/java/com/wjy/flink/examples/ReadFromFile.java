package com.wjy.flink.examples;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @ClassName ReadFromFile.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 从文件中读取数据
 * @Create 2020年07月08日 16:59:00
 */
public class ReadFromFile {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> text = env.readTextFile("file:///e://a.txt");

        DataStream<Integer> parsed = text.map(value -> Integer.valueOf(value));

        parsed.print();
        env.execute("Read From File");
    }
}
