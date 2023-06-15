package com.nabob.conch.spring.aop.overview.interceptor;

import java.lang.reflect.Method;

/**
 * 后置拦截
 *
 * @author Adam
 * @since 2023/3/14
 */
public interface AfterInterceptor {

    Object after(Object proxy, Method method, Object[] args, Object returnResult);
}
