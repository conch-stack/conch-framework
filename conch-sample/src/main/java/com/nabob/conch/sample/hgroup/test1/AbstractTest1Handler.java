package com.nabob.conch.sample.hgroup.test1;

/**
 * @author Adam
 * @since 2023/11/24
 */
public abstract class AbstractTest1Handler implements Test1Handler {
    @Override
    public void test() {
        System.out.println("==============================AbstractTest1Handler==============================");
        doTest();
    }

    public abstract void doTest();
}
