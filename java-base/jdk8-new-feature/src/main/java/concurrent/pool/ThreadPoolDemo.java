package concurrent.pool;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolDemo {
    public static void main(String[] args) throws InterruptedException {
        MyThreadPool pool = new MyThreadPool(2, new LinkedBlockingQueue<>(2));
        pool.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
