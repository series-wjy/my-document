import com.ow.ds.HiKariCPConnectionPool;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @ClassName HiKariCPTest.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年11月27日 13:40:00
 */
public class HiKariCPTest {

    @Test
    void testQuery() throws IOException {
        for(int i = 0; i < 5 ;i ++) {
            new Thread(() -> {
                System.out.println("===========================");
                HiKariCPConnectionPool.querySimple();
            }).start();
        }
        System.in.read();
    }
}
