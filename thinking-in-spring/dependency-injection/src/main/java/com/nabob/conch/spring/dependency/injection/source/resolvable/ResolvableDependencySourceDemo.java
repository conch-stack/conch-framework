package com.nabob.conch.spring.dependency.injection.source.resolvable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.PostConstruct;

/**
 * registerResolvableDependency 注册游离Bean，只能通过依赖注入的方式获取，不能通过依赖查找获取
 *
 * @author Adam
 * @date 2020/4/25
 */
public class ResolvableDependencySourceDemo {

    @Autowired
    private String value;

    @PostConstruct
    public void print() {
        System.out.println(value);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ResolvableDependencySourceDemo.class);

        // 注册BeanFactory的后置处理，在 refresh() 中的 invokeBeanFactoryPostProcessors(beanFactory)
        applicationContext.addBeanFactoryPostProcessor((beanFactory) -> {
            // 注册 Resolvable Dependency
            beanFactory.registerResolvableDependency(String.class, "test resolvable bean");
        });
        applicationContext.refresh();
        applicationContext.close();
    }
}
