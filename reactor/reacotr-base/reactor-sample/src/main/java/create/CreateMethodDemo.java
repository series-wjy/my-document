/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package create;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author wangjiayou 2019/9/10
 * @version ORAS v1.0
 */
public class CreateMethodDemo {
    public static void main(String[] args) {
        MyEventProcessor myEventProcessor = new MyEventProcessor();
        Flux<String> bridge = Flux.create(sink -> {
            myEventProcessor.register(
                    new MyEventListener<String>() {
                        public void onDataChunk(List<String> chunk) {
                            for(String s : chunk) {
                                sink.next(s);
                            }
                        }
                        public void processComplete() {
                            sink.complete();
                        }
                    });
        });
        bridge.subscribe(System.out::println);
    }
}
