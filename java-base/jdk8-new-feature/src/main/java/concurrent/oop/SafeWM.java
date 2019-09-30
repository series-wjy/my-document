package concurrent.oop;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SafeWM {

    public static void main(String[] args) {
        Executor pool = Executors.newFixedThreadPool(10);
        ThreadPoolExecutor pool2 = new ThreadPoolExecutor(4, 8, 100000, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10));

        SafeWM obj = new SafeWM();
        obj.lower.set(2);
        obj.upper.set(10);

        CountDownLatch latch = new CountDownLatch(2);
        new Thread(() -> {
            while (true) {
                latch.countDown();
                obj.setUpper(5);
                obj.get();
                obj.initialize();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                latch.countDown();
                obj.setLower(7);
                obj.get();
                obj.initialize();
            }
        }).start();
        try {
            latch.await();
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // 库存上限
    private final AtomicLong upper = new AtomicLong(0);
    // 库存下限
    private final AtomicLong lower = new AtomicLong(0);

    public synchronized void initialize() {
        lower.set(2);
        upper.set(10);
    }

    public synchronized void get() {
        if (lower.get() > upper.get()) {
            System.out.println("lower:" + lower.get() + " upper:" + upper.get());
        }
    }

    // 设置库存上限
    void setUpper(long v) {
        // 检查参数合法性
        if (v < lower.get()) {
            // throw new IllegalArgumentException();
        }
        upper.set(v);
    }

    // 设置库存下限
    void setLower(long v) {
        // 检查参数合法性
        if (v > upper.get()) {
            // throw new IllegalArgumentException();
        }
        lower.set(v);
    }
    // 省略其他业务代码
}
