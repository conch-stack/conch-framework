package com.nabob.conch.spring.aop.overview.autoproxy;

import com.nabob.conch.spring.aop.overview.aspectj.interceptor.EchoServiceMethodInterceptor;
import com.nabob.conch.spring.aop.overview.DefaultEchoService;
import com.nabob.conch.spring.aop.overview.EchoService;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * SpringBeanNameAutoProxy Demo
 * <p>
 * BeanNameAutoProxyCreator - 底层也是JDK动态代理 ； 可支持通配方式
 *
 * @author Adam
 * @since 2023/3/15
 */
public class BeanNameAutoProxyCreatorDemo {

    public static void main(String[] args) {
        // 创建 ApplicationContext 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class
        applicationContext.register(BeanNameAutoProxyCreatorDemo.class);
        applicationContext.refresh();

        // 获取代理对象
        EchoService echoServiceProxyFactoryBean = applicationContext.getBean("echoService", EchoService.class);
        echoServiceProxyFactoryBean.echo("来了");

        applicationContext.close();
    }

    @Bean
    public EchoServiceMethodInterceptor echoServiceMethodInterceptor() {
        return new EchoServiceMethodInterceptor();
    }

    @Bean
    public EchoService echoService() {
        return new DefaultEchoService();
    }

    @Bean
    public BeanNameAutoProxyCreator echoBeanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        beanNameAutoProxyCreator.setBeanNames("echo*");
        beanNameAutoProxyCreator.setInterceptorNames("echoServiceMethodInterceptor");
        return beanNameAutoProxyCreator;
    }

}
