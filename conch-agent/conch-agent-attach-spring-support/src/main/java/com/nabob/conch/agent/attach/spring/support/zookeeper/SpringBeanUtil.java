package com.nabob.conch.agent.attach.spring.support.zookeeper;


import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * spring bean
 * 如果包扫描没有包含该类，必须使用 @EnableSpringBeanUtil 注解开启才能使用
 */
public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBeanT(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        if (Objects.isNull(context)) {
            System.out.println("context is null");
        }
        return (T) context.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }
}

