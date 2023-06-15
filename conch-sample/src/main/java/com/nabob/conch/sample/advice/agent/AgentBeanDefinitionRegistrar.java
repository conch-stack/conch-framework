package com.nabob.conch.sample.advice.agent;

import com.nabob.conch.sample.advice.EnableRpcLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 注册 AgentBeanPostProcessor BeanDefinition
 */
public class AgentBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableRpcLog.class.getName()));
        String agentPackage = attributes.getString("agentPackage");
        boolean recordCk = attributes.getBoolean("recordCk");
        if (StringUtils.isNotBlank(agentPackage)) {
            HashMap<String, Object> propertyValues = new HashMap<String, Object>() {{
                put("agentPackage", agentPackage);
                put("recordCk", recordCk);
            }};
            registerBeanDefinitionIfNotExists(registry, AgentBeanPostProcessor.class, propertyValues);
        }
    }

    /**
     * 注册bean
     */
    public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, Class<?> beanClass, Map<String, Object> propertyValues) {
        String beanName = beanClass.getName();
        if (registry.containsBeanDefinition(beanName)) {
            return false;
        }

        String[] candidates = registry.getBeanDefinitionNames();

        for (String candidate : candidates) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(candidate);
            if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {
                return false;
            }
        }

        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();

        //注入属性
        if (propertyValues != null) {
            MutablePropertyValues beanPropertyValues = beanDefinition.getPropertyValues();
            for (Map.Entry<String, Object> entry : propertyValues.entrySet()) {
                beanPropertyValues.add(entry.getKey(), entry.getValue());
            }
        }

        registry.registerBeanDefinition(beanName, beanDefinition);

        return true;
    }
}
