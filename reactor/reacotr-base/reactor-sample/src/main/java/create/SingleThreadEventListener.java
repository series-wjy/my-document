package create;

import java.util.List;

interface SingleThreadEventListener<T> {
    void onDataChunk(List<T> chunk);
    void processComplete();
    void processError(Throwable e);
}