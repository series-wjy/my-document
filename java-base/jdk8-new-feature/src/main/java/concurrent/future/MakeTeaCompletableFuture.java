package concurrent.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MakeTeaCompletableFuture {

    public static void main(String[] args) {
        CompletableFuture t1 = CompletableFuture.runAsync(() -> {
            System.out.println("T1：洗水壶。。。");
            sleep(1, TimeUnit.SECONDS);
            System.out.println("T1：烧开水。。。");
            sleep(15, TimeUnit.SECONDS);
        });

        CompletableFuture t2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("T2：洗茶壶。。。");
            sleep(1, TimeUnit.SECONDS);
            System.out.println("T2：洗茶杯。。。");
            sleep(1, TimeUnit.SECONDS);
            System.out.println("T2：拿茶叶。。。");
            sleep(1, TimeUnit.SECONDS);
            return "龙井";
        });

        CompletableFuture t3 = t1.thenCombine(t2, (p, tea) -> {
            System.out.println("T1：拿到茶叶" + tea);
            sleep(1, TimeUnit.SECONDS);
            System.out.println("T1：泡茶。。。");
            return "上茶" + tea;
        });
        System.out.println(t3.join());
    }

    static void sleep(int t, TimeUnit u) {
        try {
            u.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
