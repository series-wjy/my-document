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
public class DeadLock {

    public static void main(String[] args) throws InterruptedException {
        Account2 a = new Account2(200);
        Account2 b = new Account2(200);
        Thread t1 = new Thread(() -> {
            for(int i = 0; i < 10000000; i ++) {
                a.transfer(b, 10);
            }
        });

        Thread t2 = new Thread(() -> {
            for(int i = 0; i < 10000000; i ++) {
                b.transfer(a, 10);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("a:" + a.getBalance() + " b:" + b.getBalance());
    }
}
@AllArgsConstructor
@Getter
class Account2 {
    private int balance;
    // 转账
    void transfer(Account2 target, int amt){
        synchronized (this) {
            synchronized (target) {
                if (this.balance > amt) {
                    this.balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }
}