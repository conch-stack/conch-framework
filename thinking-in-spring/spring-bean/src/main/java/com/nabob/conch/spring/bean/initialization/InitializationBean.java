package com.nabob.conch.spring.bean.initialization;

import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;

/**
 * 初始化 Spring Bean
 * @author Adam
 * @since 2020/4/3
 */
public class InitializationBean implements InitializingBean {

    @PostConstruct
    public void init() {
        System.out.println("使用：@PostConstruct 方式初始化");
    }

    public void initMethod() {
        System.out.println("使用：@Bean(initMethod) 方式初始化");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("使用：InitializingBean 方式初始化");
    }
}
