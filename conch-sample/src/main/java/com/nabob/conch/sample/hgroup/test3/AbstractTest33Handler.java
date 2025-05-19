package com.nabob.conch.sample.hgroup.test3;

/**
 * @author Adam
 * @since 2023/11/24
 */
public abstract class AbstractTest33Handler implements Test3Handler {
    @Override
    public void test() {
        System.out.println("==============================AbstractTest33Handler==============================");
        doTest();
    }

    public abstract void doTest();
}
