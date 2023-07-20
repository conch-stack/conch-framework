package com.nabob.conch.sample.dynamic;

import com.nabob.conch.sample.User;
import com.nabob.conch.sample.dynamic.aop.DynamicAnnotationBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;

/**
 * @author Adam
 * @since 2023/7/20
 */
public class DynamicConfig {

    @Bean
    public User dynamicUser1() {
        return new User("Dynamic User1", 10);
    }

    @Bean
    public User dynamicUser2() {
        return new User("Dynamic User2", 20);
    }

    /**
     * 无效
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DynamicAnnotationBeanPostProcessor dynamicAnnotationBeanPostProcessor() {
        DynamicAnnotationBeanPostProcessor bpp = new DynamicAnnotationBeanPostProcessor();
        bpp.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bpp;
    }
}
