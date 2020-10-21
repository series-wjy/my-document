package concurrent.jmm;

public class VolatileTest {
    /**
     * 有 volatile 修饰的变量
     */
//    private static volatile int COUNTER = 0;

    /**
     * 没有 volatile 修饰的变量
     */
    private static int COUNTER = 0;
    public static void main(String[] args) {
        new ChangeListener().start();
        new ChangeMaker().start();
    }

    static class ChangeListener extends Thread {
        @Override
        public void run() {
            int threadValue = COUNTER;
            while (threadValue < 5) {
                if (threadValue != COUNTER) {
                    System.out.println("Got Change for COUNTER : " + COUNTER + "");
                    threadValue = COUNTER;
                }
                // 没有 volatile 修饰时等待
//                try {
//                    Thread.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    static class ChangeMaker extends Thread {
        @Override
        public void run() {
            int threadValue = COUNTER;
            while (COUNTER < 5) {
                System.out.println("Incrementing COUNTER to : " + (threadValue + 1) + "");
                COUNTER = ++threadValue;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}