package com.nabob.conch.core.test;

/**
 * 内部类 This 使用
 *
 * @author Adam
 * @since 2021/9/22
 */
public class InnerClassThisTest {

    private String property;

    public InnerClassThisTest(String property) {
        this.property = property;

        InnerClass innerClass = new InnerClass();
        innerClass.run();
    }

    protected class InnerClass implements Runnable {

        @Override
        public void run() {
            InnerClassThisTest innerClassThisTest = InnerClassThisTest.this;
            System.out.println(innerClassThisTest.property);
        }
    }

    public static void main(String[] args) {
        new InnerClassThisTest("a");
        new InnerClassThisTest("b");
    }

}
