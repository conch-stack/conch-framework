package com.nabob.conch.sample.guavaevent;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Adam
 * @since 2024/9/2
 */
@Component
public class Test {

    @Resource
    private Subscriber1 subscriber1;

    @PostConstruct
    public void test() {
        EventBusCenter.register(subscriber1);

        EventBusCenter.post(new ExecutionEvent1("你好"));
        EventBusCenter.post(new ExecutionEvent2("江苏"));
    }
}
