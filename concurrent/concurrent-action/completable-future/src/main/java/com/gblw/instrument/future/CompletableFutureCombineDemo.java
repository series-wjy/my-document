package com.gblw.instrument.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * thenCombine 嵌套的 CompletableFuture 使用示例
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月01日 19:55:45
 */
public class CompletableFutureCombineDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Double> future = getBMI();
        Double bmi = future.get();
        System.out.println("Your BMI is:" + bmi);
    }

    private static CompletableFuture<Double> getBMI() {
        CompletableFuture<Double> heightFuture = getHeight();
        CompletableFuture<Double> weightFuture = getWeight();
        CompletableFuture<Double> bmiFuture = weightFuture.thenCombine(heightFuture, (weight, height) -> {
            Double h = Double.valueOf((height / 100));
            Double bmi = weight / (h * h);
            return bmi;
        });
        return bmiFuture;
    }

    private static CompletableFuture<Double> getWeight() {
        return CompletableFuture.supplyAsync(() -> {
            Double w = 70.0;
            System.out.println("Your weight is:" + w + "kg");
            return w;
        });
    }

    private static CompletableFuture<Double> getHeight() {
        return CompletableFuture.supplyAsync(() -> {
            Double h = 175.7;
            System.out.println("Your height is:" + h + "cm");
            return h;
        });
    }
}
