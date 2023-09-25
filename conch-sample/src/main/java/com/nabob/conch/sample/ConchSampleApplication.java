package com.nabob.conch.sample;

import com.nabob.conch.sample.advice.EnableRpcLogV2;
import com.nabob.conch.sample.byteBuddy.abtest.EnhanceAbTest;
import com.nabob.conch.sample.byteBuddy.agent.ABClientCache;
import com.nabob.conch.sample.job.ConfigurationConfigSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ConfigurationConfigSupport
//第一版
//@EnableRpcLog("com.nabob.conch.sample.dynamic")

//第二版 - 兼容第一版 - 测试
//@EnableRpcLogV2(agentPackage = "com.nabob.conch.sample.dynamic", mode = RpcLogMode.V1)
//第二版 - 第二版默认注解 - 类and方法 - Void方法拦截 - 测试
//@EnableRpcLogV2
//第二版 - 第二版自定义注解 - 类and方法 - 测试
//@EnableRpcLogV2(annotation = SelfRpcLog.class)
//第二版 - 第二版AgentPackage拦截 - 测试
@EnableRpcLogV2(agentPackage = "com.nabob.conch.sample.test")
public class ConchSampleApplication {

    @PostConstruct
    public void test() {
        try {
            EnhanceAbTest.enhance(ABClientCache.class);

            ABClientCache.getInstance();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SpringApplication.run(ConchSampleApplication.class, args);
        System.out.println("end-container-1");

//        String test = TestClassLoader.test();
//        System.out.println(test);
    }

}
