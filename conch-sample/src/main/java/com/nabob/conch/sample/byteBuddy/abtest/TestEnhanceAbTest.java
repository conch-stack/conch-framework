package com.nabob.conch.sample.byteBuddy.abtest;

import com.nabob.conch.sample.byteBuddy.agent.ABClientCache;

/**
 * @author Adam
 * @since 2023/8/10
 */
public class TestEnhanceAbTest {

    public static void main(String[] args) {
        try {
            EnhanceAbTest.enhance(ABClientCache.class);

            ABClientCache.getInstance();

            Thread.sleep(10000000000L);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

}
