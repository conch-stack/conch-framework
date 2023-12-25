package com.nabob.conch.sample.hgroup.test3;

import com.nabob.conch.sample.hgroup.HandlerGroupV3;

/**
 * @author Adam
 * @since 2023/11/24
 */
@HandlerGroupV3(groupName = "2", groupName2 = 1, strForList = {"3", "4"}, intForList = {3, 4})
public class B3TestHandler extends AbstractTest3Handler {
    @Override
    public void doTest() {
        System.out.println("B3");
    }
}
