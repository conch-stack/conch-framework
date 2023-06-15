package com.nabob.conch.spring.dependency.injection.setter.autowire;

import com.nabob.conch.spring.dependency.injection.setter.UserHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * 自动注入 autowire
 *
 * @author Adam
 * @date 2020/4/10
 */
public class AutowiringDependencySetterInjectionDemo {

    public static void main(String[] args) {
        // 创建BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // XML方式读取BeanDefinition
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:META-INF/autowiring-dependency-setter-injection.xml");

        // byName and byType
        ObjectProvider<UserHolder> beanProvider = beanFactory.getBeanProvider(UserHolder.class);
        beanProvider.stream().forEach(System.out::println);
    }
}
