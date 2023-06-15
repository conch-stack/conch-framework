package com.nabob.conch.spring.aop.overview;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 判断模式： Predicate
 * <p>
 * 查找方法
 *
 * @author Adam
 * @since 2023/3/14
 */
public class TargetMethodFilterDemo {

    public static void main(String[] args) throws ClassNotFoundException {

        String targetClassName = "com.nabob.conch.spring.aop.overview.EchoService";

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Class<?> targetClass = classLoader.loadClass(targetClassName);
        System.out.println(targetClass);

        // 查找方法
        Method targetMethod = ReflectionUtils.findMethod(targetClass, "echo", String.class);
        System.out.println(targetMethod);

        // 查找方法 - 过滤异常类型
        ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                System.out.println("only get echo method with throw NullPointerException");
            }
        }, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method method) {
                // 参数类型、异常、返回类型 等判断  | AspectJ里面也是通过这种方式进行判断的
                return method.getName().equals("echo") && method.getExceptionTypes().length == 1 && method.getExceptionTypes()[0].isAssignableFrom(NullPointerException.class);
            }
        });
    }
}
