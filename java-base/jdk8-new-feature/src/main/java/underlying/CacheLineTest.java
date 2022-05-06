package underlying;

/**
 * CPU 缓存行测试
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月29日 11:19:00
 */
public class CacheLineTest {
    public static void main(String[] args) {
        cacheLineExtentRead();
        twoDimensionalArrayInteration();
    }

    private static void twoDimensionalArrayInteration() {
        int[][] arras = new int[1024 * 8][1024 * 8];
        long start = System.currentTimeMillis();
        for(int i = 0; i < arras.length; i ++) {
            for(int j = 0; j < arras[i].length; j ++) {
                arras[i][j] = arras[i][j] * 3;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("按行迭代耗时：" + (end - start));

        start = System.currentTimeMillis();
        for(int i = 0; i < arras.length; i ++) {
            for(int j = 0; j < arras[i].length; j ++) {
                arras[j][i] = arras[j][i] * 3;
            }
        }
        end = System.currentTimeMillis();
        System.out.println("按列迭代耗时：" + (end - start));
    }

    private static void cacheLineExtentRead() {
        int[] arras = new int[64 * 1024 * 1024];
        long start = System.currentTimeMillis();
        for (int i = 0; i < arras.length; i ++) {
            arras[i] = arras[i] * 3;
        }
        long end = System.currentTimeMillis();
        System.out.println("遍历循环耗时：" + (end - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < arras.length; i += 16) {
            arras[i] = arras[i] * 3;
        }
        end = System.currentTimeMillis();
        System.out.println("每隔16个元素循环耗时：" + (end - start));
    }
}
