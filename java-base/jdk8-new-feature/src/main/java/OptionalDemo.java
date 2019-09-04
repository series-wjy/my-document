/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */

import java.util.Optional;

/**
 * @author wangjiayou 2019/9/4
 * @version ORAS v1.0
 */
public class OptionalDemo {
    public static void main(String[] args) {
        Optional o1 = Optional.empty();
        Optional o2 = Optional.of(5);
        Optional o3 = Optional.ofNullable(null);

        Optional<Company> companyOptional = Optional.empty();
        companyOptional.filter(department -> "Finance".equals(department.getName()))
                .ifPresent(x -> System.out.println("Finance is present"));
    }
}

class Company{
    String name;

    public String getName() {
        return name;
    }
}
