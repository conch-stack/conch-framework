package com.nabob.conch.dubbo.server.spring;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Adam
 * @date 2021/4/4
 */
@EnableDubbo(scanBasePackages = "com.nabob.conch.dubbo")
@SpringBootApplication
public class DubboServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboServerApplication.class, args);
    }
}
