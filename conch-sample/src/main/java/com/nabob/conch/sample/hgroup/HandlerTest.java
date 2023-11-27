package com.nabob.conch.sample.hgroup;

import com.nabob.conch.sample.hgroup.HandlerGroupRegistry;
import com.nabob.conch.sample.hgroup.test.TestHandler;
import com.nabob.conch.sample.hgroup.test1.Test1Handler;
import com.nabob.conch.sample.pipeline.test.AppointPipeline;
import com.nabob.conch.sample.pipeline.test.AppointRequestHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * @author Adam
 * @since 2023/9/18
 */
@Component
public class HandlerTest {

    @Resource
    private HandlerGroupRegistry handlerGroupRegistry;

    @PostConstruct
    void test() {
        Optional<TestHandler> a = handlerGroupRegistry.get("A", TestHandler.class);
        a.ifPresent(TestHandler::test);

        Optional<Test1Handler> b = handlerGroupRegistry.get(1, Test1Handler.class);
        b.ifPresent(Test1Handler::test);
    }

}
