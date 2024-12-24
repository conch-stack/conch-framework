package com.scorer.boot.dynamic.load.jar.deme1;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 启动时注册
 */
public class PluginImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    private final String targetUrl = "file:/D:/SpringBootPluginTest/plugins/plugin-impl-0.0.1-SNAPSHOT.jar";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassLoader classLoader = ClassLoaderUtil.getClassLoader(targetUrl);
        Class<?> clazz = null;
        try {
            clazz = classLoader.loadClass(Plugin.pluginClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(clazz.getName(), beanDefinition);
    }
}