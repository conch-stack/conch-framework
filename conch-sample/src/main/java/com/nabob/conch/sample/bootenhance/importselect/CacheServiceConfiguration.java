package com.nabob.conch.sample.bootenhance.importselect;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;

/**
 * CacheServiceConfiguration
 *
 * @author Adam
 * @since 2024/8/12
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CacheServiceConfiguration extends AbstractCacheConfiguration {

    public CacheServiceConfiguration() {
        System.out.println("CacheServiceConfiguration");
    }

    @Bean
    public MyTestBeanFactoryPostProcessor myTestBeanFactoryPostProcessor() {
        Enum<?> anEnum = enableCacheAnnotation.getEnum("mode");
        System.out.println(anEnum.name());
        return new MyTestBeanFactoryPostProcessor();
    }
}
