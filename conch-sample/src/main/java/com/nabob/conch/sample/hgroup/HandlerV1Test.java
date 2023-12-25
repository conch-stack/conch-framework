package com.nabob.conch.sample.hgroup;

import com.nabob.conch.sample.hgroup.test1.Test1Handler;
import com.nabob.conch.sample.hgroup.test2.Test2Handler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author Adam
 * @since 2023/9/18
 */
@Component
public class HandlerV1Test {

    @Resource
    private HandlerGroupRegistry handlerGroupRegistry;

//    @PostConstruct
    void test1() {
        Optional<Test1Handler> a = handlerGroupRegistry.get("1", Test1Handler.class);
        a.ifPresent(Test1Handler::test);

        Optional<Test1Handler> a1 = handlerGroupRegistry.get(2, Test1Handler.class);
        a1.ifPresent(Test1Handler::test);

        System.out.println("======================================================================================");

        Optional<Test1Handler> b = handlerGroupRegistry.get(2, Test1Handler.class);
        b.ifPresent(Test1Handler::test);

        Optional<Test1Handler> b1 = handlerGroupRegistry.get("1", Test1Handler.class);
    }

}
