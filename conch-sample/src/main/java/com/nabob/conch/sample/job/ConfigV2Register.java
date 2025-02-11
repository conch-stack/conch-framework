package com.nabob.conch.sample.job;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Adam
 * @since 2025/1/22
 */
public class ConfigV2Register implements ImportBeanDefinitionRegistrar {

    static final String QMQ_CLIENT_ANNOTATION = "QMQ_CLIENT_ANNOTATION";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        GenericBeanDefinition annotation = new GenericBeanDefinition();
        annotation.setBeanClass(ConfigV2.class);
        registry.registerBeanDefinition(QMQ_CLIENT_ANNOTATION, annotation);
    }

}
