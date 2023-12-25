package com.nabob.conch.sample.hgroup.test3;

/**
 * @author Adam
 * @since 2023/11/24
 */
public abstract class AbstractTest3Handler implements Test3Handler {
    @Override
    public void test() {
        System.out.println("==============================AbstractTest2Handler==============================");
        doTest();
    }

    public abstract void doTest();
}
