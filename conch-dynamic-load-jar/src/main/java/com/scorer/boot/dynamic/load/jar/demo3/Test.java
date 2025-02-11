package com.scorer.boot.dynamic.load.jar.demo3;

/**
 * @author Adam
 * @since 2024/12/17
 */
public class Test {

    public static void main(String[] args) throws
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException {

        ClassLoader parentClassLoader = MyClassLoader.class.getClassLoader();
        MyClassLoader classLoader = new MyClassLoader(parentClassLoader);
        Class myObjectClass = classLoader.loadClass("com.scorer.boot.dynamic.load.jar.demo3.MyObject");

        AnInterface2       object1 =
                (AnInterface2) myObjectClass.newInstance();

        MyObjectSuperClass object2 =
                (MyObjectSuperClass) myObjectClass.newInstance();

        //create new class loader so classes can be reloaded.
        classLoader = new MyClassLoader(parentClassLoader);
        myObjectClass = classLoader.loadClass("com.scorer.boot.dynamic.load.jar.demo3.MyObject");

        object1 = (AnInterface2)       myObjectClass.newInstance();
        object2 = (MyObjectSuperClass) myObjectClass.newInstance();

    }
}
