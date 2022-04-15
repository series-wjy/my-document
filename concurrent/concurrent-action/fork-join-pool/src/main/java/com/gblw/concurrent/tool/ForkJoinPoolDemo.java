package com.gblw.concurrent.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool 使用示例
 *
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月14日 13:47:00
 */
public class ForkJoinPoolDemo {

    public static void main(String[] args) {
        String[] texts = {"hello world",
                "hello me",
                "hello fork",
                "hello join",
                "fork join in world"};
        //创建ForkJoin线程池
        ForkJoinPool pool = new ForkJoinPool(3);
        //创建任务
        MR mr = new MR(texts, 0, texts.length);
        //启动任务
        Map<String, Long> result = pool.invoke(mr);
        //输出结果
        result.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    //MR模拟类
    static class MR extends RecursiveTask<Map<String, Long>> {
        private String[] arr;
        private int start, end;

        //构造函数
        MR(String[] arr, int fr, int to) {
            this.arr = arr;
            this.start = fr;
            this.end = to;
        }

        @Override
        protected Map<String, Long> compute() {
            if (end - start == 1) {
                return counts(arr[start]);
            } else {
                int mid = (start + end) / 2;
                MR mr1 = new MR(arr, start, mid);
                mr1.fork();
                MR mr2 = new MR(arr, mid, end);
                //计算子任务，并返回合并的结果
                return merge(mr2.compute(), mr1.join());
            }
        }

        //合并结果
        private Map<String, Long> merge(Map<String, Long> r1, Map<String, Long> r2) {
            Map<String, Long> result = new HashMap<>();
            result.putAll(r1);
            //合并结果
            r2.forEach((k, v) -> {
                Long c = result.get(k);
                if (c != null)
                    result.put(k, c + v);
                else
                    result.put(k, v);
            });
            return result;
        }

        //统计单词数量
        private Map<String, Long> counts(String line) {
            Map<String, Long> result = new HashMap<>();
            //分割单词
            String[] words = line.split("\\s+");
            //统计单词数量
            for (String w : words) {
                Long v = result.get(w);
                if (v != null)
                    result.put(w, v + 1);
                else
                    result.put(w, 1L);
            }
            return result;
        }
    }
}
