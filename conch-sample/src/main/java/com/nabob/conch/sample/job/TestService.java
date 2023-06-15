package com.nabob.conch.sample.job;

import org.springframework.stereotype.Service;

/**
 * @author Adam
 * @since 2022/11/15
 */
@Service
public class TestService {

    @QmqConsumer(topic = "test-topic")
    public void test(String message) {
        System.out.printf("test + %s%n", message);
    }

    @QmqConsumer(topic = "test-topic1")
    public void test1(String message) {
        System.out.printf("test1 + %s%n", message);
    }
}
