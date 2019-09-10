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
public class PushMethodDemo {
    public static void main(String[] args) {
        MyEventProcessor myEventProcessor = new MyEventProcessor();
        Flux<String> bridge = Flux.push(sink -> {
            myEventProcessor.register(
                    new SingleThreadEventListener<String>() {
                        public void onDataChunk(List<String> chunk) {
                            for(String s : chunk) {
                                sink.next(s);
                            }
                        }
                        public void processComplete() {
                            sink.complete();
                        }
                        public void processError(Throwable e) {
                            sink.error(e);
                        }
                    });
        });

        // An hybrid push/pull model
        Flux<String> bridge2 = Flux.create(sink -> {
            myEventProcessor.register(
                    new MyMessageListener<String>() {

                        public void onMessage(List<String> messages) {
                            for(String s : messages) {
                                sink.next(s);
                            }
                        }
                    });
            sink.onRequest(n -> {
                List<String> messages = myEventProcessor.getHistory(n);
                for(String s : messages) {
                    sink.next(s);
                }
            });
        });

        // Cleaning up after push() or create()
        Flux<String> bridge3 = Flux.create(sink -> {
//            sink.onRequest(n -> channel.poll(n))
//                    .onCancel(() -> channel.cancel())
//                    .onDispose(() -> channel.close())
        });
    }
}
