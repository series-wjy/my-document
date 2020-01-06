package com.ow;

import com.ow.pojo.Bar;
import com.ow.pojo.Foo;
import foo.Bar1;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.pool.TypePool;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @ClassName TestByteBuddy.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月31日 17:29:00
 */
public class TestByteBuddyTest {

    @Test
    public void test() {
        TestBytebuddy buddy = new TestBytebuddy();
        Foo foo = buddy.test4();
        assertThat(foo.m(), is("bar"));
    }

    @Test
    public void testUnloadClass() throws NoSuchFieldException {
        TypePool typePool = TypePool.Default.ofPlatformLoader();
        new ByteBuddy()
                .redefine(typePool.describe("foo.Bar1").resolve(), // do not use 'Bar.class'
                        ClassFileLocator.ForClassLoader.ofPlatformLoader())
                .defineField("qux", String.class) // we learn more about defining fields later
                .make()
                .load(ClassLoader.getSystemClassLoader());
        assertThat(Bar1.class.getDeclaredField("qux"), notNullValue());
    }
}
