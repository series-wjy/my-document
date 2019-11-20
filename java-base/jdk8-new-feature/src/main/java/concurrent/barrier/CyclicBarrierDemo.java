package concurrent.barrier;

import java.util.Vector;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CyclicBarrierDemo {
    final Vector orders = new Vector();
    final Vector delivers = new Vector();
    Executor executor = Executors.newSingleThreadExecutor();

    CyclicBarrier barrier = new CyclicBarrier(2, () -> {
        executor.execute(() -> {
            // 执行对账操作
            check();
        });
    });

    void check(){
        Object o = orders.remove(0);
        Object d = delivers.remove(0);
        // 执行对账操作
        // todo diff = check(o, d);
        // 差异写入差异库
        // todo save(diff);
    }

    public void balance() {
        // todo 存在未对账订单
        boolean isExist = true;

        new Thread(() -> {
            while (isExist) {
                // todo 查询未对账订单
                // todo orders.add(order);
            }
        }).start();
        new Thread(() -> {
            while (isExist) {
                // todo 查询未对账派送单
                // todo delivers.add(deliver);
            }
        }).start();
    }


}
