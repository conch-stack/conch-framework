package com.nabob.conch.spring.dependency.injection.setter.manual;

import com.nabob.conch.spring.dependency.injection.setter.UserHolder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * XML方式 setter 注入
 *
 * @author Adam
 * @date 2020/4/10
 */
public class XmlDepenedencySetterInjectionDemo {

    public static void main(String[] args) {

        // 创建BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // XML方式读取BeanDefinition
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:META-INF/manual-dependency-setter-injection.xml");

        UserHolder userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println(userHolder.toString());


    }
}
