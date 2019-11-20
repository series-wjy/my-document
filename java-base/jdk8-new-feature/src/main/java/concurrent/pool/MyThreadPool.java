package concurrent.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyThreadPool {
    final BlockingQueue<Runnable> queue;
    final List<Thread> works;

    public MyThreadPool(int size, LinkedBlockingQueue<Runnable> queue) {
        this.queue = queue;
        works = new ArrayList<>(size);
        for(int i = 0; i < size; i ++) {
            WorkThread workThread = new WorkThread();
            workThread.start();
            works.add(workThread);
        }
    }

    public void execute(Runnable task) throws InterruptedException {
        System.out.println("============>>插入任务");
        queue.put(task);
        System.out.println("============>>插入任务成功");
    }

    class WorkThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("============>>获取任务");
                    Runnable task = queue.take();
                    System.out.println("============>>获取任务，开始执行任务");
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
