package com.nabob.conch.spring.aop.overview.aspectj;

import com.nabob.conch.spring.aop.overview.aspectj.interceptor.EchoServiceMethodInterceptor;
import com.nabob.conch.spring.aop.overview.DefaultEchoService;
import com.nabob.conch.spring.aop.overview.EchoService;
import org.springframework.aop.framework.ProxyFactory;

/**
 * @author Adam
 * @since 2023/3/15
 */
public class ProxyFactoryDemo {

    public static void main(String[] args) {
        DefaultEchoService defaultEchoService = new DefaultEchoService();
        ProxyFactory proxyFactory = new ProxyFactory(defaultEchoService);
        proxyFactory.addAdvice(new EchoServiceMethodInterceptor());

        EchoService echoService = (EchoService) proxyFactory.getProxy();
        echoService.echo("来了");
        System.out.println(echoService.echo());
    }

}
