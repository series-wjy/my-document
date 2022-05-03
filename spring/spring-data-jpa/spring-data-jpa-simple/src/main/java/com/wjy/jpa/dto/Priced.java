package com.wjy.jpa.dto;

import com.wjy.jpa.model.Product;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月06日 09:58:00
 */

@Data
public class Priced {
    private long id;
    private Product price;

    public static Product getPrice(Product product) {
        return product;
    }
}
