package com.nabob.conch.spring.aop.overview.interceptor;

import java.lang.reflect.Method;

/**
 * 前置拦截
 *
 * @author Adam
 * @since 2023/3/14
 */
public interface BeforeInterceptor {

    Object before(Object proxy, Method method, Object[] args);
}
