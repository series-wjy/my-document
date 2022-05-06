package collections;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * HashMap 相关类测试
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月21日 10:23:00
 */
public class HashMapTest {

    public static void main(String[] args) {
        HashMap hashMap = new HashMap();
        hashMap.putIfAbsent("a", null);
        System.out.println("调用 HashMap#putIfAbsent() key not exist :" + hashMap);
        hashMap.putIfAbsent("a", 1);
        System.out.println("调用 HashMap#putIfAbsent() key exist :" + hashMap);

        hashMap.computeIfAbsent(null, (a) -> {return a;});
        System.out.println("调用 HashMap#computeIfAbsent() key not exist :" + hashMap);
        hashMap.computeIfAbsent("b", Function.identity());
        System.out.println("调用 HashMap#computeIfAbsent() key exist :" + hashMap);
        hashMap.put(null, null);
        System.out.println("hashMap :" + hashMap);

        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        //concurrentHashMap.putIfAbsent("a", null); // 抛出异常
        System.out.println("调用 concurrentHashMap#putIfAbsent() key not exist :" + concurrentHashMap);
        concurrentHashMap.putIfAbsent("a", 1);
        System.out.println("调用 concurrentHashMap#putIfAbsent() key exist :" + concurrentHashMap);

        //concurrentHashMap.computeIfAbsent(null, (a) -> {return a;}); // 抛出异常
        System.out.println("调用 concurrentHashMap#computeIfAbsent() key not exist :" + concurrentHashMap);
        concurrentHashMap.computeIfAbsent("b", (key) -> {return null;});
        System.out.println("调用 concurrentHashMap#computeIfAbsent() key exist :" + concurrentHashMap);
        //concurrentHashMap.put(null, null);
        System.out.println("concurrentHashMap :" + concurrentHashMap);
    }
}
