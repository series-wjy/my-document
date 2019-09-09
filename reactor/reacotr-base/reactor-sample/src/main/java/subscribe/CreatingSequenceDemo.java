/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package subscribe;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangjiayou 2019/9/9
 * @version ORAS v1.0
 */
public class CreatingSequenceDemo {
    public static void main(String[] args) {
        Flux<String> flux1 = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3*state);
                    if (state == 10) sink.complete();
                    return state + 1;
                });
        Flux<String> flux2 = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3*i);
                    if (i == 10) sink.complete();
                    return state;
                });
        flux2.subscribe(System.out::println);

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
