package com.ow;

import com.ow.pojo.Bar;
import com.ow.pojo.Foo;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.pool.TypePool;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @ClassName TestBytebuddy.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月31日 16:03:00
 */
public class TestBytebuddy {

    public void test() {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .with(new NamingStrategy.AbstractBase() {
                    @Override
                    protected String name(TypeDescription typeDescription) {
                        return "java.lang.Object";
                    }
                })
                .subclass(Object.class)
                .make();
        System.out.println(dynamicType.getAllTypes());
    }

    public void test2() {
        ByteBuddy byteBuddy = new ByteBuddy();
        byteBuddy.with(new NamingStrategy.SuffixingRandom("suffix"));
        DynamicType.Unloaded<?> dynamicType = byteBuddy.subclass(Object.class).make();
        System.out.println(dynamicType.getAllTypes());
    }

    public void test3() {
        Class<?> type = new ByteBuddy()
                .subclass(Object.class)
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        System.out.println(type);
    }

    public Foo test4() {
        ByteBuddyAgent.install();
        Foo foo = new Foo();
        new ByteBuddy()
                .redefine(Bar.class)
                .name(Foo.class.getName())
                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        System.out.println(foo.m().equals("bar"));
        return foo;
    }

    public void test5() {
        TypePool typePool = TypePool.Default.ofPlatformLoader();
        new ByteBuddy()
                .redefine(typePool.describe("Bar").resolve(), // do not use 'Bar.class'
                        ClassFileLocator.ForClassLoader.ofPlatformLoader())
                .defineField("qux", String.class) // we learn more about defining fields later
                .make()
                .load(ClassLoader.getSystemClassLoader());
    }

    public void test6() throws IllegalAccessException, InstantiationException {
        String toString = new ByteBuddy()
                .subclass(Object.class)
                .name("example.Type")
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance() // Java reflection API
                .toString();
        System.out.println(toString);

        toString = new ByteBuddy()
                .subclass(Object.class)
                .name("example.Type")
                .method(named("toString")).intercept(FixedValue.value("Hello World!"))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance()
                .toString();
        System.out.println(toString);

        toString = new ByteBuddy()
                .subclass(Object.class)
                .name("example.Type")
                .method(named("toString").and(returns(String.class)).and(takesArguments(5))).intercept(FixedValue.value("Hello World!"))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance()
                .toString();
        System.out.println(toString);

        Foo dynamicFoo = new ByteBuddy()
                .subclass(Foo.class)
                .method(isDeclaredBy(Foo.class)).intercept(FixedValue.value("One!"))
                .method(named("foo")).intercept(FixedValue.value("Two!"))
                .method(named("foo").and(takesArguments(1))).intercept(FixedValue.value("Three!"))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance();
        System.out.println(dynamicFoo.foo());
        System.out.println(dynamicFoo.bar());
        System.out.println(dynamicFoo.foo(new Object()));
    }
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        TestBytebuddy test = new TestBytebuddy();
        test.test6();
    }


    class Bar {
        public String m() {
            return "bar";
        }
    }
}
