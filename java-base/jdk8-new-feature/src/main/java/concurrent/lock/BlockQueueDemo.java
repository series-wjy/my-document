/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package concurrent.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wangjiayou 2019/10/11
 * @version ORAS v1.0
 */
public class BlockQueueDemo<T> {
    public static void main(String[] args) {
        BlockQueue<Integer> queue = new BlockQueue<>();
        Runnable runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i ++) {
                    queue.enq(1);
                }
            }
        };

        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(() -> {
            int i = 0;
            while (true) {
                queue.deq();
                i++;
                System.out.println(i);
            }
        }).start();
    }
}

class BlockQueue<T> {
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final List<T> list = new ArrayList<>();

    public void enq(T t) {
        lock.lock();
        try {
            while (!list.isEmpty()) {
                notFull.await();
            }
            list.add(t);
            notEmpty.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public T deq() {
        lock.lock();
        try {
            while (list.isEmpty()) {
                notEmpty.await();
            }
            notFull.signal();
            T t = list.get(list.size() - 1);
            list.remove(t);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }
}
