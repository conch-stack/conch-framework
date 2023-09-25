package com.nabob.conch.sample.dynamic.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * 拦截器
 *
 * @author Adam
 * @since 2023/3/15
 */
public class AnnotationDynamicInterceptor implements MethodInterceptor, Ordered {

    public AnnotationDynamicInterceptor() {
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return doLogAround(invocation);
    }

    /**
     * 实现aop的环绕切面
     */
    private Object doLogAround(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();
        Object response = null;
        try {
            System.out.println("Dynamic 代理开始");
            response = invocation.proceed();
            System.out.println("Dynamic 代理结束");
        } finally {
        }
        return response;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
