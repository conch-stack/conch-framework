package com.nabob.conch.sample.hgroup.test1;

import com.nabob.conch.sample.hgroup.HandlerGroup;

/**
 * @author Adam
 * @since 2023/11/24
 */
@HandlerGroup(groupName = "2")
public class B1TestHandler extends AbstractTest1Handler {
    @Override
    public void doTest() {
        System.out.println("2");
    }
}
