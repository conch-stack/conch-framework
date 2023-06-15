package com.nabob.conch.spring.ioc.overview.container;

import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Application 作为 IOC 容器
 *      注解能力的 context
 *
 * @author Adam
 * @since 2020/3/30
 */
@Configuration
public class ApplicationContextAsIoCContainerDemo {

    public static void main(String[] args) {
        // 创建 ApplicationContext 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册当前类 作为 配置类 Configuration Class
        applicationContext.register(ApplicationContextAsIoCContainerDemo.class);
        // 启动应用上下文 // TODO 重点
        applicationContext.refresh();

        // 依赖查找
        lookupCollectionType(applicationContext);

        // 停止
        applicationContext.close();
    }

    @Bean
    public User user() {
        return new User("ApplicationContext", 10);
    }

    private static void lookupCollectionType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("查找所有类型为User的Bean：" + users);
        }
    }
}
