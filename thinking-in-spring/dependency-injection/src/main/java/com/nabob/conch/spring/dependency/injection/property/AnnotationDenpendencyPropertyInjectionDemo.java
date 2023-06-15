package com.nabob.conch.spring.dependency.injection.property;

import com.nabob.conch.spring.dependency.injection.setter.UserHolder;
import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * Property 方式注入
 *
 * @author Adam
 * @date 2020/4/10
 */
public class AnnotationDenpendencyPropertyInjectionDemo {

    // TODO 会忽略静态字段的注入
    // TODO byType
    @Autowired
    private UserHolder userHolder;

    // TODO byType
    @Resource
    private UserHolder userHolder1;

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotationDenpendencyPropertyInjectionDemo.class);

        // XML方式读取BeanDefinition
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:META-INF/dependency-lookup-context.xml");

        applicationContext.refresh();

        AnnotationDenpendencyPropertyInjectionDemo propertyInjectionDemo = applicationContext.getBean(AnnotationDenpendencyPropertyInjectionDemo.class);

        // @Autowired 自动关联
        System.out.println(propertyInjectionDemo.userHolder);
        // @Resource 自动关联
        System.out.println(propertyInjectionDemo.userHolder1);

        System.out.println(propertyInjectionDemo.userHolder == propertyInjectionDemo.userHolder1);

        applicationContext.close();
    }

    /**
     * Setter
     * @param user
     * @return
     */
    @Bean
    public UserHolder userHolder(User user) {
        UserHolder userHolder = new UserHolder();
        userHolder.setUser(user);
        return userHolder;
    }

}
