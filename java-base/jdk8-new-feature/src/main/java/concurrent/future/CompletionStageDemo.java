package concurrent.future;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletionStageDemo {
    public static void main(String[] args) {
        CompletableFuture f1 = CompletableFuture.supplyAsync(() -> {
            int t = getRandom(10, 10);
            sleep(t, TimeUnit.SECONDS);
            System.out.println("f1 lasted:" + t);
            return String.valueOf(t);
        });

        CompletableFuture f2 = CompletableFuture.supplyAsync(() -> {
            int t = getRandom(10, 10);
            sleep(t, TimeUnit.SECONDS);
            System.out.println("f2 lasted:" + t);
            return String.valueOf(t);
        });

        CompletableFuture f3 = f1.applyToEither(f1, p -> p);
        System.out.println(f3.join());

        // 异常处理
        CompletableFuture<Integer> f0 =
                CompletableFuture.supplyAsync(() -> 7 / 1)
                        .thenApply(r -> r * 10)
                        .exceptionally(e -> 0);
        System.out.println(f0.join());


    }

    static int getRandom(int seed, int scope) {
        Random r = new Random(seed);
        return r.nextInt(scope);
    }

    static void sleep(int t, TimeUnit u) {
        try {
            u.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
