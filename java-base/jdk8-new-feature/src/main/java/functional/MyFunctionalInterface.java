/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package functional;

/**
 * @author wangjiayou 2019/9/4
 * @version ORAS v1.0
 */
@FunctionalInterface
public interface MyFunctionalInterface {

    default void defaultMethod() {
        // do something
    }

    static void staticMethod() {
        // do something
    }

    void method1();

    int hashCode();

    boolean equals(Object o);

    String toString();
}
