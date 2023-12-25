package com.nabob.conch.sample.hgroup.test2;

import com.nabob.conch.sample.hgroup.HandlerGroupV2;

/**
 * @author Adam
 * @since 2023/11/24
 */
@HandlerGroupV2(intForList = {11,12})
public class A2TestHandler extends AbstractTest2Handler {
    @Override
    public void doTest() {
        System.out.println("11 or 12");
    }
}
