package com.gblw.instrument.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * thenCompose 嵌套的 CompletableFuture 使用示例
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月01日 19:55:45
 */
public class CompletableFutureComposeDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        CompletableFuture<CompletableFuture<String>> future1 = plainChainInvoke();
//        String result1 = future1.get().get();
//        System.out.println(result1);

        CompletableFuture<String> future2 = thenCompose();
        String result2 = future2.get();
        System.out.println(result2);
    }

    private static CompletableFuture<CompletableFuture<String>> plainChainInvoke() {
        CompletableFuture<CompletableFuture<String>> future = getOrderInfo("xxxx-xxxx-xxxx")
                .thenApply(res -> getProductInfo(res));
        return future;
    }

    private static CompletableFuture<String> thenCompose() {
        CompletableFuture<String> future = getOrderInfo("xxxx-xxxx-xxxx")
                .thenCompose(order -> getProductInfo(order));
        return future;
    }

    private static CompletableFuture<Order> getOrderInfo(String orderId) {
        return CompletableFuture.supplyAsync(() -> {
            Order order = new Order();
            order.setOrderId(orderId);
            order.setOrderName("新冠疫苗订单");
            return order;
        });
    }

    private static CompletableFuture<String> getProductInfo(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            return order.getOrderName() + "订单包括：10000支XXX疫苗";
        });
    }

    static class Order {
        private String orderId;
        private String orderName;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getOrderName() {
            return orderName;
        }

        public void setOrderName(String orderName) {
            this.orderName = orderName;
        }
    }
}
