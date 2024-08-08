package com.nabob.conch.sample.bootenhance.frunner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ApplicationRunner 时机: 容器刷新完成后
 *
 * 比 CommandLineRunner 先跑
 *
 * 1. 执行@PostConstruct注解的方法 - init()...
 * 2. 执行实现InitializingBean接口的方法 - afterPropertiesSet()...
 * 3. 执行实现ApplicationRunner接口的方法 - run(ApplicationArguments args)...
 * 4. 执行实现CommandLineRunner接口的方法 - run(String... args)...
 *
 * @author Adam
 * @since 2024/8/8
 */
@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("-----------------------[MyApplicationRunner]  扩展点演示 # run  开始--------------------------------------");
        System.out.println("[MyApplicationRunner] # run ; "+"时机：此时已经刷新容器处于run方法的后半部分了 接下来run方法将发布running事件");
        System.out.println("-----------------------[MyApplicationRunner]  扩展点演示 # run  结束--------------------------------------");
        System.out.println();
    }
}
