package com.nabob.conch.sample.bootenhance.frunner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner 时机: 容器刷新完成后
 *
 * 比 ApplicationRunner 后跑
 *
 * @author Adam
 * @since 2024/8/8
 */
@Component
public class MyCommandLineRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("-----------------------[MyCommandLineRunner]  扩展点演示 # run  开始--------------------------------------");
        System.out.println("[MyCommandLineRunner] # run ; "+"时机：此时已经刷新容器处于run方法的后半部分了 接下来run方法将发布running事件");
        System.out.println("-----------------------[MyCommandLineRunner]  扩展点演示 # run  结束--------------------------------------");
        System.out.println();
    }
}
