/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package concurrent.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangjiayou 2019/9/17
 * @version ORAS v1.0
 */
public class LockMultiResources {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        Account1 a = new Account1(200, lock);
        Account1 b = new Account1(200, lock);
        Account1 c = new Account1(200, lock);

        Thread thread2 = new Thread(() -> {
            a.transfer(b, 100);
        });
        Thread thread1 = new Thread(() -> {
            b.transfer(c, 100);
        });
        thread1.start();
        thread2.start();
        Thread.sleep(100);
        System.out.println("a:" + a.getBalance() + " b:" + b.getBalance());
    }
}

@AllArgsConstructor
@Getter
class Account1 {
    private int balance;
    private Object lock;
    // 转账
    void transfer(Account1 target, int amt){
        synchronized (lock) {
            if (this.balance > amt) {
                this.balance -= amt;
                target.balance += amt;
            }
        }
    }
}