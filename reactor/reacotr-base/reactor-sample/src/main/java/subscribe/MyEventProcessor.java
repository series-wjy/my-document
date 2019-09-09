/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package subscribe;

import java.util.Arrays;
import java.util.List;

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
}
