package com.nabob.conch.sample.hgroup.test;

/**
 * @author Adam
 * @since 2023/11/24
 */
public abstract class AbstractTestHandler implements TestHandler{
    @Override
    public void test() {
        System.out.println("==============================AbstractTestHandler==============================");
        doTest();
    }

    public abstract void doTest();
}
