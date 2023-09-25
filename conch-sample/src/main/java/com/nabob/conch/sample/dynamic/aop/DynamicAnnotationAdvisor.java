package com.nabob.conch.sample.dynamic.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * RpcLog 注解 切片
 *
 * @author Adam
 * @since 2023/3/15
 */
public class DynamicAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    /**
     * 切面
     */
    private Advice advice;

    /**
     * 切点
     */
    private Pointcut pointcut;

    public DynamicAnnotationAdvisor() {
        Set<Class<? extends Annotation>> selfAnnotationTypes = new LinkedHashSet<>(1);
        selfAnnotationTypes.add(Dynamic.class);

        this.advice = buildAdvice();
        this.pointcut = buildPointcut(selfAnnotationTypes);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    protected Advice buildAdvice() {
        AnnotationDynamicInterceptor interceptor = new AnnotationDynamicInterceptor();
        return interceptor;
    }

    private Pointcut buildPointcut(Set<Class<? extends Annotation>> selfAnnotationTypes) {
        // ComposablePointcut 可利用这个类进行 类过滤；方法匹配；pointcut匹配
        ComposablePointcut result = null;
        // 注解匹配方式
        for (Class<? extends Annotation> selfAnnotationType : selfAnnotationTypes) {
            // 类匹配 - 注解了 selfAnnotationType 的类
            Pointcut cpc = new AnnotationMatchingPointcut(selfAnnotationType, true);
            // 方法匹配 - 注解了 selfAnnotationType 的方法
            Pointcut mpc = new AnnotationMatchingPointcut(null, selfAnnotationType, true);
            if (result == null) {
                result = new ComposablePointcut(cpc);
            } else {
                result.union(cpc);
            }
            result = result.union(mpc);
        }
        return (result != null ? result : Pointcut.TRUE);
    }
}
