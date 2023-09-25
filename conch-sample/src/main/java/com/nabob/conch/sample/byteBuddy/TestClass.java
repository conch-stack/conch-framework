package com.nabob.conch.sample.byteBuddy;

/**
 * @author Adam
 * @since 2023/8/9
 */
public class TestClass {

    public static String getOid() {
        return "TestClass#getOid-static";
    }

    public String getOid1() {
        return "TestClass#getOid1";
    }
}
