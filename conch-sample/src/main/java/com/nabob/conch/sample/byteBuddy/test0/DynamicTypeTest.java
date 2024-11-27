package com.nabob.conch.sample.byteBuddy.test0;

import com.nabob.conch.sample.enfinal.TestFinalClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Adam
 * @since 2023/8/10
 */
public class DynamicTypeTest {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
        //  Cannot subclass primitive, array or final types: class com.nabob.conch.sample.enfinal.TestFinalClass
//        testFinalClass();

//        testClass();

//        testMethodDelegation();

//        testAddNewMethodAndField();

        testCallSuper();
    }

    private static void testCallSuper() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {

        DynamicType.Unloaded<TestClass> dynamicTypeIntercepted = new ByteBuddy()
                .subclass(TestClass.class)
                .method(ElementMatchers.named("getOid1").and(ElementMatchers.isDeclaredBy(TestClass.class)).and(ElementMatchers.returns(String.class)))
                // TestClass2 中的方法 与 getOid1 和 getOid 完全不同
                .intercept(MethodDelegation.to(TestClass2.class).andThen(SuperMethodCall.INSTANCE))
                .make();

        Class<? extends TestClass> loaded = dynamicTypeIntercepted.load(DynamicTypeTest.class.getClassLoader()).getLoaded();
        TestClass testClass = loaded.newInstance();
        System.out.println(testClass.getOid1());
    }

    private static void testAddNewMethodAndField() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {

        Class<? extends TestClass> loaded = new ByteBuddy().subclass(TestClass.class)
                .name("MyTestClass")
                .defineMethod("custom", String.class, Modifier.PUBLIC)
                .intercept(MethodDelegation.to(TestClass1.class))
                .defineField("x", String.class, Modifier.PUBLIC)
                .make()
                .load(DynamicTypeTest.class.getClassLoader())
                .getLoaded();

        TestClass testClass = loaded.newInstance();

        // 反射调用
        Method custom = loaded.getDeclaredMethod("custom", null);

        System.out.println(custom.invoke(testClass));
        Field x = loaded.getDeclaredField("x");
        System.out.println( x != null);
    }

    public static void testMethodDelegation() throws InstantiationException, IllegalAccessException {
        /**
         * 调用 TestClass#getOid1 方法，实际会调用 TestClass1#getOid 方法
         * ByteBuddy 如何知道调用 Class 中的那个方法：
         * - 依据方法签名、返回值类型、方法名、注解 的顺序来匹配方法（越后面的优先级越高）
         *
         * 如果在TestClass1.class中有超过一个可调用的方法的签名和返回类型一致，
         * 我们可以使用@BindingPriority来解决冲突。@BindingPriority有一个整型参数-这个值越大优先级越高。
         */
        DynamicType.Unloaded<TestClass> dynamicTypeIntercepted = new ByteBuddy()
                .subclass(TestClass.class)
                .method(ElementMatchers.named("getOid1").and(ElementMatchers.isDeclaredBy(TestClass.class)).and(ElementMatchers.returns(String.class)))
                .intercept(MethodDelegation.to(TestClass1.class))
                .make();

        Class<? extends TestClass> loaded = dynamicTypeIntercepted.load(DynamicTypeTest.class.getClassLoader()).getLoaded();
        TestClass testClass = loaded.newInstance();
        System.out.println(testClass.getOid1());
    }

    public static void testClass() throws InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<TestClass> dynamicTypeIntercepted = new ByteBuddy()
                .subclass(TestClass.class)
//                .method(ElementMatchers.isToString())
//                .method(ElementMatchers.named("toString"))
//                .method(ElementMatchers.named("getOid1"))
//                .method(ElementMatchers.namedOneOf("getOid1", "toString"))
                // 静态方法不生效
//                .method(ElementMatchers.named("getOid"))
                .method(ElementMatchers.named("getOid1").and(ElementMatchers.isDeclaredBy(TestClass.class)).and(ElementMatchers.returns(String.class)))
                .intercept(FixedValue.value("Dynamic Type Intercepted"))
                .make();

        Class<? extends TestClass> loaded = dynamicTypeIntercepted.load(DynamicTypeTest.class.getClassLoader()).getLoaded();
        TestClass testClass = loaded.newInstance();
        System.out.println(testClass.getOid1());
        System.out.println(testClass.getOid());
        System.out.println(testClass.toString());
    }

    public static void testFinalClass() throws InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<TestFinalClass> dynamicTypeIntercepted = new ByteBuddy()
                .subclass(TestFinalClass.class)
                .method(ElementMatchers.any())
                .intercept(FixedValue.value("Dynamic Type Intercepted"))
                .make();

        Class<? extends TestFinalClass> loaded = dynamicTypeIntercepted.load(DynamicTypeTest.class.getClassLoader()).getLoaded();
        TestFinalClass testFinalClass = loaded.newInstance();
        System.out.println(testFinalClass.getOid1());
        System.out.println(testFinalClass.getOid());
    }

}
