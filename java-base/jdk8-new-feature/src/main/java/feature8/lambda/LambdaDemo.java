/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package feature8.lambda;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author wangjiayou 2019/9/4
 * @version ORAS v1.0
 */
public class LambdaDemo {
    public static void main (String[] ar){
        Employee[] employees  = {
                new Employee("David"),
                new Employee("Naveen"),
                new Employee("Alex"),
                new Employee("Richard")};

        System.out.println("Before Sorting Names: "+ Arrays.toString(employees));
        Arrays.sort(employees, Employee::nameCompare);
        System.out.println("After Sorting Names "+Arrays.toString(employees));
    }
}

class Employee {
    String name;

    Employee(String name) {
        this.name = name;
    }

    public static int nameCompare(Employee a1, Employee a2) {
        return a1.name.compareTo(a2.name);
    }

    public String toString() {
        return name;
    }
}

class MyComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        return 0;
    }
}

