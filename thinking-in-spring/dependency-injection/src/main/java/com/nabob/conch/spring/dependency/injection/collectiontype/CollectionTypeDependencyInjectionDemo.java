package com.nabob.conch.spring.dependency.injection.collectiontype;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * @author Adam
 * @date 2020/4/14
 */
public class CollectionTypeDependencyInjectionDemo {

    public static void main(String[] args) {

        // 创建BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // XML方式读取BeanDefinition
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:META-INF/basictype-dependency-injection.xml");

        ObjectProvider<UserThree> beanProvider = beanFactory.getBeanProvider(UserThree.class);
        UserThree userThree = beanProvider.getIfAvailable();
        System.out.println(userThree);

        UserThree userThree1 = beanFactory.getBean("userThree1", UserThree.class);
        System.out.println(userThree1);
    }
}
