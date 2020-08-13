package com.wjy.flink.examples;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @ClassName ReadFromFile.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 从文件中读取数据
 * @Create 2020年07月08日 16:59:00
 */
public class AssignKey {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> text = env.readTextFile("file:///e://b.txt");

        WindowedStream<Tuple2<String, Integer>, Tuple, TimeWindow> parsed = text
                .flatMap(new Splitter())
                .keyBy(0)
                .window(EventTimeSessionWindows.withGap(Time.seconds(10)));

        SingleOutputStreamOperator<Double> aggregate = parsed.aggregate(new SumAggregate());
        aggregate.print().setParallelism(1);
        env.execute("Assign Key");
    }

    private static class SumAggregate implements AggregateFunction<Tuple2<String, Integer>, Tuple2<Integer, Integer>, Double> {


        @Override
        public Tuple2<Integer, Integer> createAccumulator() {
            return new Tuple2<>(0, 0);
        }

        @Override
        public Tuple2<Integer, Integer> add(Tuple2<String, Integer> value, Tuple2<Integer, Integer> accumulator) {
            return new Tuple2<>(accumulator.f0 + value.f1, accumulator.f1 + 1);
        }

        @Override
        public Double getResult(Tuple2<Integer, Integer> accumulator) {
            return ((double) accumulator.f0) / accumulator.f1;
        }

        @Override
        public Tuple2<Integer, Integer> merge(Tuple2<Integer, Integer> a, Tuple2<Integer, Integer> b) {
            return new Tuple2<>(a.f0 + b.f0, a.f1 + b.f1);
        }
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            String[] words = sentence.split(" ");
            out.collect(new Tuple2<>(words[0], Integer.valueOf(words[1])));
        }
    }
}
