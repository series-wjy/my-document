package concurrent.threadlocal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocalDemo {

    final static AtomicInteger nextId = new AtomicInteger();
    final static ThreadLocal<Integer> tl = ThreadLocal.withInitial(() -> nextId.getAndIncrement());

    public static void main(String[] args) {
        ThreadLocalDemo demo = new ThreadLocalDemo();
        new Thread(() -> {
            System.out.println(SafeDateFormat.get());
        }).start();

        new Thread(() -> {
            System.out.println(SafeDateFormat.get());
        }).start();
    }


    static class SafeDateFormat {
        //定义ThreadLocal变量
        static final ThreadLocal<DateFormat>
                tl=ThreadLocal.withInitial(
                ()-> new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss"));

        static DateFormat get(){
            return tl.get();
        }
    }
    //不同线程执行下面代码
    //返回的df是不同的
    DateFormat df = SafeDateFormat.get();
}
