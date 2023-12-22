package com.nabob.conch.sample.hgroup;

import com.nabob.conch.sample.User;
import com.nabob.conch.sample.hgroup.HandlerGroupRegistry;
import com.nabob.conch.sample.hgroup.test.TestHandler;
import com.nabob.conch.sample.hgroup.test1.Test1Handler;
import com.nabob.conch.sample.pipeline.PipelineHelper;
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
    @Resource
    private PipelineHelper pipelineHelper;

    @PostConstruct
    void test() {
        Optional<TestHandler> a = handlerGroupRegistry.get("A", TestHandler.class);
        a.ifPresent(TestHandler::test);

        Optional<TestHandler> a1 = handlerGroupRegistry.get("A", TestHandler.class);
        a1.ifPresent(TestHandler::test);

        Optional<Test1Handler> b = handlerGroupRegistry.get(1, Test1Handler.class);
        b.ifPresent(Test1Handler::test);

        Optional<Test1Handler> b1 = handlerGroupRegistry.get(1, Test1Handler.class);
        b1.ifPresent(Test1Handler::test);

        AppointRequestHolder appointRequestHolder = new AppointRequestHolder();
        try {
            Optional<PipelineHelper> bad = handlerGroupRegistry.get(1, PipelineHelper.class);
            bad.ifPresent(target -> {
                System.out.println(target.call("b", appointRequestHolder, AppointRequestHolder.class, Boolean.class));
            });

            Optional<PipelineHelper> bad1 = handlerGroupRegistry.get(1, PipelineHelper.class);
            bad1.ifPresent(target -> {
                System.out.println(target.call("b", appointRequestHolder, AppointRequestHolder.class, Boolean.class));
            });

        } catch (Exception e) {
            System.out.println("出错了");
            e.printStackTrace();
        }

        System.out.println(pipelineHelper.call("b", appointRequestHolder, AppointRequestHolder.class, Boolean.class));

    }

}
