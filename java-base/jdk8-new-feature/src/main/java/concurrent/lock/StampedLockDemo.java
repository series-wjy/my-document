package concurrent.lock;

import java.util.concurrent.locks.StampedLock;

public class StampedLockDemo {

    final StampedLock lock = new StampedLock();

    public void testLock() {
        long stamp = lock.readLock();
        try {

        } finally {
            lock.unlock(stamp);
        }
    }

    private long x, y;
    public void testOptimisticLock() {
        // 获取乐观锁
        long stamp = lock.tryOptimisticRead();
        long curx = x, cury = y;
        // 验证这期间是否存在写操作
        if(!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                curx = x;
                cury = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
    }

    public static void main(String[] args) {
        StampedLockDemo demo = new StampedLockDemo();
    }
}
