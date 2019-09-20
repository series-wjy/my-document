/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package feature8.stream;

import lombok.Data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author wangjiayou 2019/9/6
 * @version ORAS v1.0
 */
public class DistinctStream {
    public static void main(String[] args) {
        sample();
        distinctByKey();
    }

    private static void distinctByKey() {
        Person lokesh = new Person(1, "Lokesh", "Gupta");
        Person brian = new Person(2, "Brian", "Clooney");
        Person alex = new Person(3, "Alex", "Kolen");

        //Add some random persons
        Collection<Person> list = Arrays.asList(lokesh,brian,alex,lokesh,brian,lokesh);
        // Get distinct objects by key
        List<Person> distinctElements = list.stream()
                .filter( distinctByKey(p -> p.getId()) )
                .collect( Collectors.toList() );

        // Let's verify distinct elements
        System.out.println( distinctElements );
    }

    //Utility function
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private static void sample() {
        Collection<String> list = Arrays.asList("A", "B", "C", "D", "A", "B", "C");

        // Get collection without duplicate i.e. distinct only
        List<String> distinctElements = list.stream().distinct().collect(Collectors.toList());

        //Let's verify distinct elements
        System.out.println(distinctElements);
    }

    //Model class
    @Data
    static class Person
    {
        public Person(Integer id, String fname, String lname) {
            super();
            this.id = id;
            this.fname = fname;
            this.lname = lname;
        }

        private Integer id;
        private String fname;
        private String lname;

        //Getters and Setters

        @Override
        public String toString() {
            return "Person [id=" + id + ", fname=" + fname + ", lname=" + lname + "]";
        }
    }
}
