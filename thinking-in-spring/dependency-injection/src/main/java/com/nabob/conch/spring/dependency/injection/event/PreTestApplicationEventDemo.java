package com.nabob.conch.spring.dependency.injection.event;

import com.nabob.conch.spring.dependency.injection.basictype.UserTwo;
import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 提前尝试一下 Spring 事件机制
 *
 * @author Adam
 * @date 2020/4/21
 */
public class PreTestApplicationEventDemo implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        if (eventPublisher == null) {
            eventPublisher = applicationEventPublisher;
        }
    }

    /**
     * 发布事件
     *
     * @param event 事件
     */
    public static void publishEvent(ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }


    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(PreTestApplicationEventDemo.class);

        applicationContext.refresh();

        PreTestApplicationEventDemo.publishEvent(new UserEvent(applicationContext, new User("event-user", 10)));
        PreTestApplicationEventDemo.publishEvent(new UserTwoEvent(applicationContext, new UserTwo(true, "event-userTwo")));

        applicationContext.close();
    }

    /**
     * 注册 事件监听器
     */
    @Bean
    public UsersEventListener usersEventListener() {
        return new UsersEventListener();
    }
}
