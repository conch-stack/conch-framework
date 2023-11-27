package com.nabob.conch.sample.hgroup.test;

import com.nabob.conch.sample.hgroup.HandlerGroup;

/**
 * @author Adam
 * @since 2023/11/24
 */
@HandlerGroup(groupName = "B")
public class BTestHandler extends AbstractTestHandler {
    @Override
    public void doTest() {
        System.out.println("B");
    }
}
