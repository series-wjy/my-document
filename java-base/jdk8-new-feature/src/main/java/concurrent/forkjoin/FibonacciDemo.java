package concurrent.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class FibonacciDemo {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool(4);
        Fibonacci task = new Fibonacci(5);
        // 启动分治任务
        Integer result = pool.invoke(task);
        System.out.println(result);

        System.out.println(f1(6));

    }

    static class Fibonacci extends RecursiveTask<Integer> {
        int n;
        public Fibonacci(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if(n <= 1) {
                return n;
            }
            Fibonacci f1 = new Fibonacci(n - 1);
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);
            return f2.compute() + f1.join();
        }
    }


    public static int f1(int n) {
        if(n < 1) {
            return 0;
        }else if(n == 1 || n == 2) {
            return 1;
        }

        return f1(n-1) + f1(n-2);
    }
}
