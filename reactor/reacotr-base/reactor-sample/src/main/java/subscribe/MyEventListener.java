package subscribe;

import java.util.List;

interface MyEventListener<T> {
    void onDataChunk(List<T> chunk);
    void processComplete();
}