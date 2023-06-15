package com.nabob.conch.spring.bean.scope;

import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * 2. 注册 ThreadLocalScope + 使用
 *
 * @author Adam
 * @date 2020/4/27
 */
public class ThreadLocalScopeDemo {

    /**
     * 注入
     */
    @Bean
    @Scope(ThreadLocalScope.SCOPE_NAME)
    public User user() {
        return buildUser();
    }

    private static User buildUser() {
        User user = new User();
        user.setName(String.valueOf(System.nanoTime()));
        return user;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ThreadLocalScopeDemo.class);

        // 2. 注册 自定义 Scope
        applicationContext.addBeanFactoryPostProcessor(beanFactory -> {
            beanFactory.registerScope(ThreadLocalScope.SCOPE_NAME, new ThreadLocalScope());
        });

        applicationContext.refresh();

        // demo
        scopedBeanByLookupOnThread(applicationContext);
        scopedBeanByLookupMultiThread(applicationContext);

        applicationContext.close();
    }

    /**
     * 同一个线程
     */
    public static void scopedBeanByLookupOnThread(AnnotationConfigApplicationContext applicationContext) {
        for (int i = 0; i < 3; i++) {
            User user = applicationContext.getBean("user", User.class);
            System.out.printf("ThreadId: %d : user= %s %n", Thread.currentThread().getId(), user);
        }
    }

    /**
     * 多个线程
     */
    public static void scopedBeanByLookupMultiThread(AnnotationConfigApplicationContext applicationContext) {
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(() -> {
                User user = applicationContext.getBean("user", User.class);
                System.out.printf("ThreadId: %d : user= %s %n", Thread.currentThread().getId(), user);
            });

            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
