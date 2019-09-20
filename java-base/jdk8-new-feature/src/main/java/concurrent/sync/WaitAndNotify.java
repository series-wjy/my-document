/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package concurrent.sync;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author wangjiayou 2019/9/20
 * @version ORAS v1.0
 */
public class WaitAndNotify {
    public static void main(String[] args) {
        Doctor doctor = new Doctor();
        Patient p1 = new Patient("病人1", doctor);
        Patient p2 = new Patient("病人2", doctor);
        Patient p3 = new Patient("病人3", doctor);

        new Thread(p1).start();
        new Thread(p2).start();
        new Thread(p3).start();
    }
}

@RequiredArgsConstructor
@Getter
class Patient implements Runnable {
    @NonNull
    private String name;
    @NonNull
    private Doctor doctor;

    @Override
    public void run() {
        while (!doctor.isIdle()) {
            System.out.println("医生烟抽完了，终于TM轮到我了。。。。。。");
            doctor.inspect(this);
            System.out.println("医生忙成狗了，病人耐心等吧。。。。。。");
        }
    }
}

@Getter
class Doctor {
    private boolean isIdle = true;

    public void inspect(Patient p) {
        synchronized (this) {
            isIdle = false;
            System.out.println("医生现在在给病人：" + p.getName() + "看病，其他病人请等待。。。。。。");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                isIdle = true;
                System.out.println("医生现在不忙了，抽根烟。。。。。。");
                this.notifyAll();
            }
        }
    }
}