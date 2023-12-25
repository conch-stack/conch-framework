package com.nabob.conch.sample.hgroup;

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
public class HandlerV2Test {

    @Resource
    private HandlerGroupRegistryV2 handlerGroupRegistryV2;

//    @PostConstruct
    void test2() {
        Optional<Test2Handler> a = handlerGroupRegistryV2.get("11", Test2Handler.class);
        a.ifPresent(Test2Handler::test);

        Optional<Test2Handler> a1 = handlerGroupRegistryV2.get("12", Test2Handler.class);
        a1.ifPresent(Test2Handler::test);

        Optional<Test2Handler> b = handlerGroupRegistryV2.get(11, Test2Handler.class);
        b.ifPresent(Test2Handler::test);

        Optional<Test2Handler> b1 = handlerGroupRegistryV2.get(12, Test2Handler.class);
        b1.ifPresent(Test2Handler::test);

        Optional<Test2Handler> d = handlerGroupRegistryV2.get(13, Test2Handler.class);
        d.ifPresent(Test2Handler::test);

        Optional<Test2Handler> d1 = handlerGroupRegistryV2.get(14, Test2Handler.class);
        d1.ifPresent(Test2Handler::test);

        Optional<Test2Handler> c = handlerGroupRegistryV2.get("13", Test2Handler.class);
        c.ifPresent(Test2Handler::test);

        Optional<Test2Handler> c1 = handlerGroupRegistryV2.get("14", Test2Handler.class);
        c1.ifPresent(Test2Handler::test);

    }

}
