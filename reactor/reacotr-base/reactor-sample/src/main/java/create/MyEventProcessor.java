/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package create;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongConsumer;

/**
 * @author wangjiayou 2019/9/9
 * @version ORAS v1.0
 */
public class MyEventProcessor {

    void register(MyEventListener<String> listener) {
        List<String> chunk = Arrays.asList("1","2","3","4","5");
        listener.onDataChunk(chunk);
        listener.processComplete();
    }

    void register(SingleThreadEventListener<String> listener) {
        List<String> chunk = Arrays.asList("1","2","3","4","5");
        listener.onDataChunk(chunk);
        listener.processComplete();
    }

    void register(MyMessageListener<String> listener) {
        List<String> chunk = Arrays.asList("1","2","3","4","5");
        listener.onMessage(chunk);
    }

    public List<String> getHistory(Long l) {
        return new ArrayList<String>();
    }
}
