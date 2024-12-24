package com.nabob.conch.sample;

import com.google.common.collect.Lists;
import com.nabob.conch.sample.uitl.ReflectionHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TestJvmType {

    /**
     * 通配符泛型 - 2
     */
    public static void testWildcardType2() {
        TestJvmType testJvmType = new TestJvmType();
        testJvmType.wildcardType2(new TestType3(), new TestType4(), new TestType5(), new TestType6());
    }

    /**
     * testWildcardType call 2
     *
     * @param params params
     */
    public void wildcardType2(TestType2<?>... params) {
        for (TestType2<?> param : params) {
            Class<?> aClass = param.getClass();
            System.out.println(aClass.getTypeName());
            for (Type genericInterface : aClass.getGenericInterfaces()) {
                if (genericInterface instanceof ParameterizedType) {
                    System.out.println("ParameterizedType 1 ActualTypeArguments：" + genericInterface.getTypeName());
                    ParameterizedType pType = (ParameterizedType) genericInterface;
                    for (Type actualTypeArgument : pType.getActualTypeArguments()) {
                        System.out.println("\t类型：" + actualTypeArgument.getTypeName());

                      if (actualTypeArgument instanceof ParameterizedType) {
                        System.out.println("ParameterizedType 2 ActualTypeArguments：" + actualTypeArgument.getTypeName());
                        ParameterizedType pType2 = (ParameterizedType) actualTypeArgument;
                        for (Type actualTypeArgument2 : pType2.getActualTypeArguments()) {
                          System.out.println("\t类型：" + actualTypeArgument2.getTypeName());
                        }
                      }
                    }
                    System.out.println("===============================================");
                }
            }
        }
    }

    public static void main(String[] args) throws NoSuchFieldException {
//        testWildcardType2();

        List<String> s = new ArrayList<>();
        s.add("ddd");
        System.out.println(s.get(0).getClass().getName());
        System.out.println(s.get(0).getClass().getTypeName());
        System.out.println(s.get(0).getClass().getCanonicalName());
        System.out.println(s.get(0).getClass().getSimpleName());

    }

    public static class TestType<T extends String> {
        private T t;

        public TestType(T t) {
            this.t = t;
        }

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }

    public interface TestType2<T> { }
    public static class TestType3 implements TestType2<String> { }
    public static class TestType4 implements TestType2<Integer> { }
    public static class TestType5 implements TestType2<Boolean> { }
    public static class TestType6 implements TestType2<TestType<String>> { }
}