package com.nabob.conch.spring.ioc.overview.injection;

import com.nabob.conch.spring.ioc.overview.domain.User;
import com.nabob.conch.spring.ioc.overview.repository.UserRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 依赖查找
 *
 * @author Adam
 * @since 2020/3/30
 */
public class DependencyInjectionDemo {

    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:META-INF/dependency-injection-context.xml");
        // ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/META-INF/dependency-lookup-context.xml");

        // 自定义 Bean
        UserRepository userRepository = (UserRepository)beanFactory.getBean("userRepository");
        System.out.println(userRepository.toString());

        // 不相等： 内建依赖
        System.out.println(userRepository.getBeanFactory() == beanFactory);

        // 延迟加载
        ObjectFactory<User> objectFactory = userRepository.getObjectFactory();
        System.out.println(objectFactory.getObject());

        // 容器内建 Bean
        Environment environment = beanFactory.getBean(Environment.class);
        System.out.println("获取Environment类型的Bean：" + environment);

    }
}
