package concurrent.barrier;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CountDownLathDemo {

    Executor executor = Executors.newFixedThreadPool(2);
    public void blance() {
        // 是否存在未对账订单
        boolean isExists = true;
        while(isExists) {
            CountDownLatch latch = new CountDownLatch(2);
            executor.execute(() -> {
                // todo 查询未对账订单
                latch.countDown();
            });

            executor.execute(() -> {
                // todo 查询派送单
                latch.countDown();
            });
            // 等待上两步操作完成
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // todo 执行对账操作
            // todo 差异数据入库
        }
    }
}
