package concurrent.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class MakeTeaFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> task2 = new FutureTask<>(new T2Task());
        FutureTask<String> task1 = new FutureTask<>(new T1Task(task2));

        new Thread(task2).start();
        Thread tea = new Thread(task1);
        tea.start();
        System.out.println(task1.get());
    }


    /**
     * 烧水任务
     */
    static class T1Task implements Callable<String>{
        FutureTask<String> t1Future;

        public T1Task(FutureTask<String> t1Future) {
            this.t1Future = t1Future;
        }

        @Override
        public String call() throws Exception {
            System.out.println("T1：洗水壶。。。");
            TimeUnit.SECONDS.sleep(1);
            System.out.println("T1：烧开水。。。");
            TimeUnit.SECONDS.sleep(15);
            String tea = t1Future.get();
            System.out.println("T1：拿到茶叶" + tea);
            TimeUnit.SECONDS.sleep(1);
            System.out.println("T1：泡茶。。。");
            return "上茶：" + tea;
        }
    }

    /**
     * 洗茶壶、洗茶杯、拿茶叶
     */
    static class T2Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("T2：洗茶壶。。。");
            TimeUnit.SECONDS.sleep(1);
            System.out.println("T2：洗茶杯。。。");
            TimeUnit.SECONDS.sleep(1);
            System.out.println("T2：拿茶叶。。。");
            TimeUnit.SECONDS.sleep(1);
            return "龙井";
        }
    }
}
