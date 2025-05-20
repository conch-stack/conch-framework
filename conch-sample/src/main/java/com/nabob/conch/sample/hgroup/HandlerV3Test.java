package com.nabob.conch.sample.hgroup;

import com.nabob.conch.sample.hgroup.test3.AbstractTest33Handler;
import com.nabob.conch.sample.hgroup.test3.AbstractTest3Handler;
import com.nabob.conch.sample.hgroup.test3.Test3Handler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author Adam
 * @since 2023/9/18
 */
@Component
public class HandlerV3Test {

    @Resource
    private HandlerGroupRegistryV3 handlerGroupRegistryV3;

//    @PostConstruct
    void test2() {
        Optional<Test3Handler> a = handlerGroupRegistryV3.get("1", Test3Handler.class);
        a.ifPresent(Test3Handler::test);

        Optional<Test3Handler> a1 = handlerGroupRegistryV3.get("2", Test3Handler.class);
        a1.ifPresent(Test3Handler::test);
        Optional<Test3Handler> a2 = handlerGroupRegistryV3.get("3", Test3Handler.class);
        a2.ifPresent(Test3Handler::test);
        Optional<Test3Handler> a3 = handlerGroupRegistryV3.get("4", Test3Handler.class);
        a3.ifPresent(Test3Handler::test);

        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");

        Optional<Test3Handler> b = handlerGroupRegistryV3.get(1, Test3Handler.class);
        b.ifPresent(Test3Handler::test);
        Optional<Test3Handler> b1 = handlerGroupRegistryV3.get(2, Test3Handler.class);
        b1.ifPresent(Test3Handler::test);
        Optional<Test3Handler> b2 = handlerGroupRegistryV3.get(3, Test3Handler.class);
        b2.ifPresent(Test3Handler::test);
        Optional<Test3Handler> b3 = handlerGroupRegistryV3.get(4, Test3Handler.class);
        b3.ifPresent(Test3Handler::test);

        Optional<Test3Handler> d = handlerGroupRegistryV3.get(13, Test3Handler.class);
        d.ifPresent(Test3Handler::test);
        Optional<Test3Handler> d1 = handlerGroupRegistryV3.get(14, Test3Handler.class);
        d1.ifPresent(Test3Handler::test);
        Optional<Test3Handler> c = handlerGroupRegistryV3.get("13", Test3Handler.class);
        c.ifPresent(Test3Handler::test);
        Optional<Test3Handler> c1 = handlerGroupRegistryV3.get("14", Test3Handler.class);
        c1.ifPresent(Test3Handler::test);
    }

    @PostConstruct
    void test3() {
        Optional<Test3Handler> a = handlerGroupRegistryV3.get("1", Test3Handler.class);
        a.ifPresent(Test3Handler::test);

        Optional<AbstractTest3Handler> b = handlerGroupRegistryV3.get("1", AbstractTest3Handler.class);
        b.ifPresent(AbstractTest3Handler::test);

        Optional<AbstractTest33Handler> c = handlerGroupRegistryV3.get("1", AbstractTest33Handler.class);
        c.ifPresent(AbstractTest33Handler::test);
        System.out.println("end");
    }
}
