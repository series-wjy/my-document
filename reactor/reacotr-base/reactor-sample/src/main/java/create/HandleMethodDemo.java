/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package create;

import reactor.core.publisher.Flux;

/**
 * @author wangjiayou 2019/9/10
 * @version ORAS v1.0
 */
public class HandleMethodDemo {
    public static void main(String[] args) {
        Flux<String> alphabet = Flux.just(-1, 30, 13, 9, 20)
                .handle((i, sink) -> {
                    String letter = alphabet(i);
                    if (letter != null)
                        sink.next(letter);
                });

        alphabet.subscribe(System.out::println);
    }

    public static String alphabet(int letterNumber) {
        if (letterNumber < 1 || letterNumber > 26) {
            return null;
        }
        int letterIndexAscii = 'A' + letterNumber - 1;
        return "" + (char) letterIndexAscii;
    }
}
