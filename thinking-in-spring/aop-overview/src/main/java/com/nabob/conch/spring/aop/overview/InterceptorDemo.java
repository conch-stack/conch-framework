package com.nabob.conch.spring.aop.overview;

import com.nabob.conch.spring.aop.overview.interceptor.AfterInterceptor;
import com.nabob.conch.spring.aop.overview.interceptor.BeforeInterceptor;
import com.nabob.conch.spring.aop.overview.proxy.ProxyEchoService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 拦截器模式
 *
 * @author Adam
 * @since 2023/3/14
 */
public class InterceptorDemo {

    public static void main(String[] args) {

        // 前置模式 - 动态代理方式
        EchoService echoService = (EchoService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{EchoService.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (Void.TYPE.equals(method.getReturnType())) {
                    ProxyEchoService proxyEchoService = new ProxyEchoService();
                    // 前置 before
                    BeforeInterceptor beforeInterceptor = new BeforeInterceptor() {
                        @Override
                        public Object before(Object proxy, Method method, Object[] args) {
                            System.out.println("before");
                            return System.currentTimeMillis();
                        }
                    };
                    Long before = (Long) beforeInterceptor.before(proxy, method, args);
                    proxyEchoService.echo((String) args[0]);
                    // 后置 after
                    AfterInterceptor afterInterceptor = new AfterInterceptor() {
                        @Override
                        public Object after(Object proxy, Method method, Object[] args, Object returnResult) {
                            System.out.println("after");
                            System.out.println(System.currentTimeMillis() - before);
                            return returnResult;
                        }
                    };
                    return afterInterceptor.after(proxy, method, args, null);
                }
                return null;
            }
        });

        echoService.echo("你来拉");

    }
}
