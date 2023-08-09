package com.nabob.conch.sample.enfinal;

/**
 * @author Adam
 * @since 2023/8/9
 */
public class Test {

    public static void main(String[] args) {

        System.out.println(TestFinalClass.getOid());

        TestFinalClassEnhanceUtil.enhance(TestFinalClass.class);

        System.out.println(TestFinalClass.getOid());
    }
}
