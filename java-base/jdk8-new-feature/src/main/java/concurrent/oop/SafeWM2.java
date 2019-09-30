package concurrent.oop;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class SafeWM2 {

    private final AtomicReference<Boundary> reference = new AtomicReference<>();

    public void setBoundary(Boundary boundary) {
        Boundary old = reference.get();
        if (reference.compareAndSet(old, boundary)) {
            System.out.println("lower:" + boundary.getLower() + " upper:" + boundary.getUpper());
        }
    }

    public static void main(String[] args) {
        SafeWM2 obj = new SafeWM2();
        CountDownLatch latch = new CountDownLatch(2);
        new Thread(() -> {
            while (true) {
                latch.countDown();

            }
        }).start();

        new Thread(() -> {
            while (true) {
                latch.countDown();

            }
        }).start();
        try {
            latch.await();
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // 省略其他业务代码
}

class Boundary {
    private final long lower;
    private final long upper;

    public long getLower() {
        return lower;
    }

    public long getUpper() {
        return upper;
    }

    public Boundary(long lower, long upper) {
        if (lower > upper) {
            throw new IllegalArgumentException();
        }
        this.lower = lower;
        this.upper = upper;
    }
}