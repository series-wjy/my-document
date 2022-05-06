package executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月18日 14:55:00
 */
public class ScheduleExecutorDemo {
    public static void main(String[] args) {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        for (int i = 0; i < 10; i ++) {
            Runnable task = new TestTask("测试任务" + i);
            ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);
//            try {
////                System.out.print("输出执行结果" + scheduledFuture.get());
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            } catch (ExecutionException e) {
////                e.printStackTrace();
////            }
        }
    }

    static class TestTask implements Runnable {
        String name;

        public TestTask(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println("线程执行完成" + name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
