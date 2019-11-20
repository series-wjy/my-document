package concurrent.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyCache<K, V> {

    private final Map<K, V> cache;

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private final Lock r = rwl.readLock();

    private final Lock w = rwl.writeLock();

    public MyCache() {
        cache = new HashMap<>(16);
    }

    public V get(K k) {
        r.lock();
        V v = null;
        try {
            v = cache.get(k);
            if(v != null) {
                return v;
            }
        } finally {
            r.unlock();
        }

        w.lock();
        try {
            /**
             * 再次验证缓存是否存在
             * 原因：高并发下，多个线程竞争写锁，一个线程获取写锁，其他线程等待，如果Key都相同
             * 前一个线程将缓存写入后，后面的线程就不需要再查询数据库了
             */
            v = cache.get(k);
            if(v == null) {
                // todo 查询数据库，加载数据
                if(v != null) {
                    put(k, v);
                }
            }
            return v;
        } finally {
            w.unlock();
        }
    }

    public void put(K k, V v) {
        w.lock();
        try {
            cache.put(k, v);
        } finally {
            w.unlock();
        }
    }
}
