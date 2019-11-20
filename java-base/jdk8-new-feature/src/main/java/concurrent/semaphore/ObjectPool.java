package concurrent.semaphore;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

public class ObjectPool<T, R> {

    private List<T> pool;
    private Semaphore sem;

    public ObjectPool(int size, T t) {
        pool = new Vector<>(size);
        for(int i = 0; i < size; i ++) {
            pool.add(t);
        }
        sem = new Semaphore(size);
    }

    R exec(Function<T, R> func) {
        T t = null;
        try {
            sem.acquire();
            t = pool.remove(0);
            return func.apply(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            pool.add(t);
            sem.release();
        }
    }
}

class Task {
    private String name;
    public Task(int name) {
        this.name = "Task " + name;
    }
}