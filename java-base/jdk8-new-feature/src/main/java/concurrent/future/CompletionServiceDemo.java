package concurrent.future;

import java.util.concurrent.*;

public class CompletionServiceDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Executor executor = Executors.newFixedThreadPool(3);
        CompletionService<String> service = new ExecutorCompletionService(executor);

        service.submit(() -> getPrice("s1", 1, TimeUnit.SECONDS));
        service.submit(() -> getPrice("s2", 5, TimeUnit.SECONDS));
        service.submit(() -> getPrice("s3", 2, TimeUnit.SECONDS));

        for (int i = 0; i < 3; i++) {
            String r = service.take().get();
            System.out.println(r);
        }
    }

    static String getPrice(String s, int t, TimeUnit u) {
        sleep(t, u);
        String ret = "询价类型：" + s;
        return ret;
    }

    static void sleep(int t, TimeUnit u) {
        try {
            u.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
