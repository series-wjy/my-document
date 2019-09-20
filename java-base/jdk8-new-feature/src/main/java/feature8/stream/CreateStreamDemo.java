/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package feature8.stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author wangjiayou 2019/9/3
 * @version ORAS v1.0
 */
public class CreateStreamDemo {
    public static void main(String[] args) {
        createStream();
    }

    public static void createStream() {
        // create by ele
        Stream<Integer> stream1 = Stream.of(1,2,3,4,5,6,7,8,9);
        stream1.forEach(p -> System.out.println(p));

        // create by array
        Stream<Integer> stream2 = Stream.of( new Integer[]{1,2,3,4,5,6,7,8,9} );
        stream2.forEach(p -> System.out.println(p));

        // create by list
        List<Integer> list = new ArrayList<>();
        for(int i = 1; i< 10; i++){
            list.add(i);
        }
        Stream<Integer> stream3 = list.stream();
        stream3.forEach(p -> System.out.println(p));

        // create by generate or iterate
        Stream<Date> stream4 = Stream.generate(() -> { return new Date(); });
        stream4 = Stream.iterate(new Date(), x -> {
            x.setTime(0);
            return x;
        });
//        stream4.forEach(p -> System.out.println(p));

        // create by char or string token
        IntStream stream5 = "12345_abcdefg".chars();
        stream5.forEach(p -> System.out.println(p));
        //OR
        Stream<String> stream6 = Stream.of("A$B$C".split("\\$"));
        stream6.forEach(p -> System.out.println(p));



    }
}
