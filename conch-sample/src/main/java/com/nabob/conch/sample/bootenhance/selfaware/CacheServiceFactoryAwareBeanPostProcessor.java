package com.nabob.conch.sample.bootenhance.selfaware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Objects;

/**
 * CacheServiceFactoryAwareBeanPostProcessor
 *
 * @author Adam
 * @since 2024/8/12
 */
public class CacheServiceFactoryAwareBeanPostProcessor implements BeanPostProcessor {

    private final CacheServiceFactory cacheServiceFactory;

    public CacheServiceFactoryAwareBeanPostProcessor(CacheServiceFactory cacheServiceFactory) {
        this.cacheServiceFactory = cacheServiceFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        if (Objects.nonNull(this.cacheServiceFactory) && bean instanceof CacheServiceFactoryAware) {
            ((CacheServiceFactoryAware) bean).setCacheServiceFactory(this.cacheServiceFactory);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        return bean;
    }
}
