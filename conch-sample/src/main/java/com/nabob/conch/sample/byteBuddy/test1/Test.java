package com.nabob.conch.sample.byteBuddy.test1;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.Serializable;

/**
 * @author Adam
 * @since 2024/11/27
 */
public class Test {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .implement(Serializable.class)
                .name("HelloWorld")
                .method(ElementMatchers.named("toString"))
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
}
