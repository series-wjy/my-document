package concurrent.semaphore;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class SemaphoreDemo {

    private int counts;

    private final Semaphore semaphore = new Semaphore(1);

    public void addCounts() {
        try {
            semaphore.acquire();
            counts += 1;
            System.out.println("current counts:" + counts);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public static void main(String[] args) {
        SemaphoreDemo demo = new SemaphoreDemo();
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0 ; i < 10; i ++)
            demo.addCounts();
        }).start();
        new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0 ; i < 10; i ++)
            demo.addCounts();
        }).start();
        new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0 ; i < 10; i ++)
            demo.addCounts();
        }).start();

        latch.countDown();
    }
}
