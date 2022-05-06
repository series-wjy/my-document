package underlying;

public class BranchPrediction {
    public static void main(String args[]) {
        int i = 0;
        int j = 0;
        int k = 0;
        long start = System.currentTimeMillis();
        for (; i < 1000; i++) {
            for (; j <10000; j ++) {
                for (; k < 100000; k++) {
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Time spent is " + (end - start));

        i = 0;
        j = 0;
        k = 0;
        start = System.currentTimeMillis();
        for (; i < 100000; i++) {
            for (; j <10000; j ++) {
                for (; k < 1000; k++) {
                }
            }
        }
        end = System.currentTimeMillis();
        System.out.println("Time spent is " + (end - start) + "ms");
    }
}