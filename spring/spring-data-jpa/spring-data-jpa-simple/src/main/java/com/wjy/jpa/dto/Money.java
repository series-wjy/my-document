package com.wjy.jpa.dto;

import com.wjy.jpa.model.Product;

import java.math.BigDecimal;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月06日 10:27:00
 */
public class Money {

    public static Product of(int i) {
        return Product.builder().price(new BigDecimal(i)).build();
    }
}
