package com.autumn.framework;
import org.junit.jupiter.api.Test;

/**
 * @ClassName ClassUtilTest.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 测试类加载工具
 * @Create 2020年04月25日 22:05:00
 */
public class ClassUtilTest {

    @Test
    public void testClassLoad() {
        System.out.println(String.class.getName());
        System.out.println(String.class.getSimpleName());

        Class c = TestStatic.class;
    }
}
