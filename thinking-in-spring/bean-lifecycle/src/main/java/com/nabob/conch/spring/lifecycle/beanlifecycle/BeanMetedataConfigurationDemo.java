package com.nabob.conch.spring.lifecycle.beanlifecycle;

import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Bean 元信息配置
 *
 * @author Adam
 * @date 2020/4/27
 */
public class BeanMetedataConfigurationDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanMetedataConfigurationDemo.class);
        // XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);

        // Properties 加载
        PropertiesBeanDefinitionReader propertiesBeanDefinitionReader = new PropertiesBeanDefinitionReader(applicationContext);

        // ClassPath Resource 设置 ： 不需要 "classpath:"
//        Resource resource = new ClassPathResource("user.properties");
//        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");

        int numDefinitions = propertiesBeanDefinitionReader.loadBeanDefinitions("classpath:user.properties");
        System.out.println("加载的BeanDefinition个数：" + numDefinitions);

        applicationContext.refresh();


        User user = applicationContext.getBean("user", User.class);
        User factoryUser = applicationContext.getBean("factory_user", User.class);
        System.out.println(user);
        System.out.println(factoryUser);

        applicationContext.close();
    }

    @Bean("factory_user")
    public FactoryBean<User> user() {
        return new FactoryBean<User>() {
            @Override
            public User getObject() throws Exception {
                return new User();
            }

            @Override
            public Class<?> getObjectType() {
                return User.class;
            }
        };
    }

}
