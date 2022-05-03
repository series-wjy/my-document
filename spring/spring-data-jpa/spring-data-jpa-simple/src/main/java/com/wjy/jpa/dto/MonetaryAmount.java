package com.wjy.jpa.dto;

import com.wjy.jpa.model.Product;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月06日 09:52:00
 */
@Data
public class MonetaryAmount extends Product {

    private BigDecimal Amount;

    public static MonetaryAmount add(Product p1, Product p2) {
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        BigDecimal amount = monetaryAmount.getAmount();
        amount.add(p1.getPrice());
        amount.add(p2.getPrice());
        return monetaryAmount;
    }
}
