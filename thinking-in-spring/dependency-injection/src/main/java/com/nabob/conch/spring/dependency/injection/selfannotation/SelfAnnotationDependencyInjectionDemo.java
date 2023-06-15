package com.nabob.conch.spring.dependency.injection.selfannotation;

import com.nabob.conch.spring.dependency.injection.setter.UserHolder;
import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

/**
 * 自定义注解 实现依赖注入
 *
 * @author Adam
 * @date 2020/4/21
 */
public class SelfAnnotationDependencyInjectionDemo {

    @Autowired
    private UserHolder userHolder;

    @Resource
    private UserHolder userHolder1;

    @InjectUser
    private UserHolder userHolder2;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(SelfAnnotationDependencyInjectionDemo.class);
        // XML方式读取BeanDefinition
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:META-INF/dependency-lookup-context.xml");
        applicationContext.refresh();

        SelfAnnotationDependencyInjectionDemo propertyInjectionDemo = applicationContext.getBean(SelfAnnotationDependencyInjectionDemo.class);
        // @Autowired 自动关联
        System.out.println(propertyInjectionDemo.userHolder);
        // @Resource 自动关联
        System.out.println(propertyInjectionDemo.userHolder1);

        // @InjectUser 自定义注解
        System.out.println(propertyInjectionDemo.userHolder2);

        applicationContext.close();
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 3)
    public static AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
        AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        autowiredAnnotationBeanPostProcessor.setAutowiredAnnotationType(InjectUser.class);
        return autowiredAnnotationBeanPostProcessor;
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
