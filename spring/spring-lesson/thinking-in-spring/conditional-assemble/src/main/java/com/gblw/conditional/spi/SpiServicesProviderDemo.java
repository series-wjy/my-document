package com.gblw.conditional.spi;

import java.util.ServiceLoader;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 13:22:00
 */
public class SpiServicesProviderDemo {

    public static void main(String[] args) {
        ServiceLoader<MyDataSource> sl = ServiceLoader.load(MyDataSource.class);
        sl.forEach(System.out :: println);
    }
}
