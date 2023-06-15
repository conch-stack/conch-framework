package com.nabob.conch.dubbo.client.spring;

import com.nabob.conch.dubbo.facade.TestService;
import com.nabob.conch.dubbo.facade.User;
import com.nabob.conch.dubbo.facade.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @author Adam
 * @date 2021/4/4
 */
@EnableDubbo
@SpringBootApplication
public class DubboClientApplication {

    @DubboReference
    UserService userService;

    @DubboReference
    TestService testService;

    public static void main(String[] args) {
        SpringApplication.run(DubboClientApplication.class, args);
    }

    @PostConstruct
    public void testUserService() {
        User userById = userService.getUserById();
        System.out.println(userById);

        String testName = testService.getTestName();
        System.out.println(testName);
    }
}
