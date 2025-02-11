package com.nabob.conch.sample.bootenhance.dinitialization;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;

/**
 * 初始化 Spring Bean
 *
 * 如果三个定义在一个Bean中，执行顺序为：  @PostConstuct -> InitializingBean -> 自定义初始化方法
 *
 * @author Adam
 * @since 2020/4/3
 */
public class InitializationBean implements BeanFactoryAware, InitializingBean {

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

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }
}
