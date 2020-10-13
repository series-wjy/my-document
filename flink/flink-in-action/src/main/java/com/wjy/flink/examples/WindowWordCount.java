package com.wjy.flink.examples;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class WindowWordCount {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        DataStream<Tuple2<String, Integer>> dataStream = env
//                .socketTextStream("192.168.56.32", 9999)
//                .flatMap(new Splitter())
//                .keyBy(0)
//                .timeWindow(Time.seconds(5))
//                .sum(1);

//        dataStream.print();

        DataStream<Tuple2<String, Integer>> wordCounts = env.fromElements(
                new Tuple2<String, Integer>("hello", 1),
                new Tuple2<String, Integer>("world", 2));

        wordCounts.map(new MapFunction<Tuple2<String, Integer>, Integer>() {
            @Override
            public Integer map(Tuple2<String, Integer> value) throws Exception {
                return value.f1;
            }
        });

        wordCounts.keyBy(0);
        wordCounts.print();

        env.execute("Window WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

}