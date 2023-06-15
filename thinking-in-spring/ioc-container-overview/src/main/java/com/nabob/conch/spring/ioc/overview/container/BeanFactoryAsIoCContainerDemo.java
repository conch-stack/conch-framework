package com.nabob.conch.spring.ioc.overview.container;

import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.util.Map;

/**
 * BeanFactory 作为 IOC 容器
 *
 * TODO 用这个就没有那些事件等复杂的支持了
 *
 * @author Adam
 * @since 2020/3/30
 */
public class BeanFactoryAsIoCContainerDemo {

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 加载配置
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        int beansNum = reader.loadBeanDefinitions("classpath:META-INF/dependency-lookup-context.xml");
        System.out.println("加载的Bean的个数：" + beansNum);

        // 依赖查找
        lookupCollectionType(beanFactory);
    }


    private static void lookupCollectionType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("查找所有类型为User的Bean：" + users);
        }
    }
}
