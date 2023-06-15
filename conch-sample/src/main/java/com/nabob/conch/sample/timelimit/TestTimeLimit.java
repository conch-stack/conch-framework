package com.nabob.conch.sample.timelimit;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Adam
 * @since 2022/11/18
 */
@Component
public class TestTimeLimit {

    @Resource
    private TestConsumerManager testConsumerManager;

    public void test(String key, int targetTimeout) {
        System.out.println("创建对象" + key);
        testConsumerManager.addNewDynamicQmqTestConsumer(key, targetTimeout);
    }

}
