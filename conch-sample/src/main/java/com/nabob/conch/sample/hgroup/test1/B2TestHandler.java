package com.nabob.conch.sample.hgroup.test1;

import com.nabob.conch.sample.hgroup.HandlerGroup;

/**
 * @author Adam
 * @since 2023/11/24
 */
@HandlerGroup(groupName2 = 2)
public class B2TestHandler extends AbstractTest1Handler {
    @Override
    public void doTest() {
        System.out.println("2");
    }
}
