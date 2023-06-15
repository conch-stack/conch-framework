package com.nabob.conch.spring.lifecycle.beanlifecycle.cycledepend;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 注册 类 或 包 ：
 *      类或包中类上的注解会被扫描
 *
 * @author Adam
 * @date 2020/4/30
 */
public class TestCycleDependenceDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(TestCycleDependenceDemo.class);

        applicationContext.refresh();


        TestA testA = applicationContext.getBean("testA", TestA.class);
        TestB testB = applicationContext.getBean("testB", TestB.class);

        System.out.println(testA.getTestB());
        System.out.println(testB.getTestA());

        applicationContext.close();


    }

    @Bean
    public TestA testA() {
        TestA testA = new TestA();
        testA.setName("我是A");
        return testA;
    }

    @Bean
    public TestB testB() {
        TestB testB = new TestB();
        testB.setName("我是B");
        return testB;
    }
}
