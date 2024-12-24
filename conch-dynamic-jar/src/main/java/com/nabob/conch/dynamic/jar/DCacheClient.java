package com.nabob.conch.dynamic.jar;

/**
 * @author Adam
 * @since 2024/12/18
 */
public class DCacheClient extends DirectDalClient {

    @Override
    public void test() {
        DCacheHelper helper = new DCacheHelper();
        helper.doHelper();

        DCacheHolder.doHolder();

        System.out.println("First DCacheClient");
//        System.out.println("Second DCacheClient");
    }
}
