package com.nabob.conch.sample.byteBuddy.test1;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.Serializable;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author Adam
 * @since 2024/11/27
 */
public class Test {

    public Test() throws InstantiationException, IllegalAccessException {
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .implement(Serializable.class)
                .name("HelloWorld")
                .method(named("toString"))
//                .intercept(FixedValue.value("Hello World!"))
                .intercept(MethodDelegation.to(new LogInterceptor()))
                .make()
                .load(Test.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        System.out.println(dynamicType.newInstance().toString());

        // 存在重载的方法话，可以添加更多方法描述
//        ElementMatchers.named("a")
//                .and(ElementMatchers.returns(String.class))
//                .and(ElementMatchers.takesArguments(1));
    }

    class Foo {
        public String bar() { return null; }
        public String foo() { return null; }
        public String foo(Object o) { return null; }
    }

    Foo dynamicFoo = new ByteBuddy()
            .subclass(Foo.class)
            // 匹配由Foo.class声明的方法
            .method(isDeclaredBy(Foo.class)).intercept(FixedValue.value("One!"))
            // 匹配名为foo的方法
            .method(named("foo")).intercept(FixedValue.value("Two!"))
            // 匹配名为foo，入参数量为1的方法
            .method(named("foo").and(takesArguments(1))).intercept(FixedValue.value("Three!"))
            .make()
            .load(getClass().getClassLoader())
            .getLoaded()
            .newInstance();
}
