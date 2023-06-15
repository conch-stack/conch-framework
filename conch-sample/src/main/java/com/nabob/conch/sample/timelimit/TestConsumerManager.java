package com.nabob.conch.sample.timelimit;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Adam
 * @since 2022/11/29
 */
@Component
public class TestConsumerManager {

    private static final Map<String, TestConsumer> DYNAMIC_QMQ_TEST_CONSUMER_MAP = new ConcurrentHashMap<>();

    private static final Timer timer = new HashedWheelTimer(new DefaultThreadFactory("dynamic-qmq-test-consumer-timer"),
            1, TimeUnit.MILLISECONDS, 1024);

    public void addNewDynamicQmqTestConsumer(String key, int targetTimeout) {
        if (DYNAMIC_QMQ_TEST_CONSUMER_MAP.containsKey(key)) {
            return;
        }

        Optional<TestConsumer> testConsumerOptional = TestConsumer.getInstance(key);
        if (!testConsumerOptional.isPresent()) {
            return;
        }
        TestConsumer testConsumer = testConsumerOptional.get();
        DYNAMIC_QMQ_TEST_CONSUMER_MAP.put(key, testConsumer);

        // 开启
        testConsumer.start();
        timer.newTimeout(timeout -> {
            System.out.println(String.format("TestConsumerManager: %s Timeout stopping", key));
            testConsumer.stop();
            DYNAMIC_QMQ_TEST_CONSUMER_MAP.remove(key);
            System.out.println(String.format("TestConsumerManager: %s Timeout stopped", key));
        }, targetTimeout, TimeUnit.SECONDS);
    }

}
