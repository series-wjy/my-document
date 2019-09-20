package concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentGlobalField {
    private volatile long count = 0;

    private static AtomicInteger count2 = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final ConcurrentGlobalField test = new ConcurrentGlobalField();
        System.out.println(test.calc(test));
        System.out.println(count2.get());
    }

    private void add10K() {
        int idx = 0;
        while (idx++ < 10000) {
            count += 1;
            count2.getAndAdd(1);
        }
    }

    public long calc(ConcurrentGlobalField test) throws InterruptedException {
        // 创建两个线程，执行 add() 操作
        Thread th1 = new Thread(() -> {
            test.add10K();
        });
        Thread th2 = new Thread(() -> {
            test.add10K();
        });
        // 启动两个线程
        th1.start();
        th2.start();
        // 等待两个线程执行结束
        th1.join();
        th2.join();
        return count;
    }
}
