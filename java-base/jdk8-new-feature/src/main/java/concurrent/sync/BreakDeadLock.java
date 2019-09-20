/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package concurrent.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjiayou 2019/9/17
 * @version ORAS v1.0
 */
public class BreakDeadLock {
    public static void main(String[] args) throws InterruptedException {
        Account3 a = new Account3(200);
        Account3 b = new Account3(200);
        Account3 c = new Account3(200);
        Thread t1 = new Thread(() -> {
            for(int i = 0; i < 10; i ++) {
                a.transfer(b, 10);
            }
        });

        Thread t2 = new Thread(() -> {
            for(int i = 0; i < 10; i ++) {
                b.transfer(c, 10);
            }
        });
        Thread t3 = new Thread(() -> {
            for(int i = 0; i < 10; i ++) {
                c.transfer(a, 10);
            }
        });
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        System.out.println("a:" + a.getBalance() + " b:" + b.getBalance() + " c:" + c.getBalance());
    }

}

/**
 * 同时获取两把锁，避免持有且等待条件发生
 */
@RequiredArgsConstructor
@Getter
class Account3 {
    // actr 应该为单例
    private Allocator actr = Allocator.getInstance();
    @NonNull
    private int balance;
    // 转账
    void transfer(Account3 target, int amt) {
        // 一次性申请转出账户和转入账户，直到成功
        while (!actr.apply(this, target));
        try {
            // 锁定转出账户
            synchronized (this) {
                // 锁定转入账户
                synchronized (target) {
                    if (this.balance > amt) {
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            }
        } finally {
            actr.free(this, target);
        }
    }
}

/**
 * 按顺序加锁，避免循环等待死锁发生
 */
@AllArgsConstructor
@Getter
class Account4 {
    private int id;
    private int balance;
    // 转账
    void transfer(Account4 target, int amt){
        Account4 left = this;
        Account4 right = target;
        if (this.id > target.id) {
            left = target;
            right = this;
        }
        // 锁定序号小的账户
        synchronized(left){
            // 锁定序号大的账户
            synchronized(right){
                if (this.balance > amt){
                    this.balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }
}

class Allocator {
    private List<Object> als = new ArrayList<>();
    private static Allocator instance = new Allocator();

    private Allocator() {

    }

    public static Allocator getInstance() {
        return instance;
    }

    // 一次性申请所有资源
    synchronized boolean apply(Object from, Object to) {
        if (als.contains(from) || als.contains(to)) {
            try {
                System.out.println("转账账户加锁失败，等待其他转账完成。。。。。。");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            System.out.println("转账账户加锁成功，准备开始转账。。。。。。");
            als.add(from);
            als.add(to);
        }
        return true;
    }

    // 归还资源
    synchronized void free(Object from, Object to) {
        System.out.println("转账完成，通知其他转账等待队列。。。。。。");
        als.remove(from);
        als.remove(to);
        notifyAll();
    }
}

