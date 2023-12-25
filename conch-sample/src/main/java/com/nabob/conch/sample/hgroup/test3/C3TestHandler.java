package com.nabob.conch.sample.hgroup.test3;

import com.nabob.conch.sample.hgroup.HandlerGroupV3;

/**
 * @author Adam
 * @since 2023/11/24
 */
@HandlerGroupV3(groupName = "3")
public class C3TestHandler extends AbstractTest3Handler {
    @Override
    public void doTest() {
        System.out.println("C3");
    }
}
