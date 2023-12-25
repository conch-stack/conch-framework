package com.nabob.conch.sample.hgroup.test2;

/**
 * @author Adam
 * @since 2023/11/24
 */
public abstract class AbstractTest2Handler implements Test2Handler {
    @Override
    public void test() {
        System.out.println("==============================AbstractTest2Handler==============================");
        doTest();
    }

    public abstract void doTest();
}
