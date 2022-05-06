package com.gblw.conditional.spi;

import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 14:14:00
 */
public class SpringSpiProviderDemo {
    public static void main(String[] args) {
        List<MyDataSource> myDataSources = SpringFactoriesLoader
                .loadFactories(MyDataSource.class, SpringFactoriesLoader.class.getClassLoader());
        myDataSources.forEach(System.out::println);

        List<String> strings = SpringFactoriesLoader
                .loadFactoryNames(MyDataSource.class, SpringFactoriesLoader.class.getClassLoader());
        strings.forEach(System.out::println);
    }
}
