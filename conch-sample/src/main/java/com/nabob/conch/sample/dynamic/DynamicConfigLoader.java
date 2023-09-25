package com.nabob.conch.sample.dynamic;

import com.nabob.conch.sample.dynamic.aop.DynamicAnnotationBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Adam
 * @since 2023/7/20
 */
@Component
public class DynamicConfigLoader implements ApplicationContextAware, BeanFactoryAware {
    private ConfigurableListableBeanFactory beanFactory;
    private GenericApplicationContext genericApplicationContext;

    private BeanDefinitionRegistry registry;
    private AnnotatedBeanDefinitionReader reader;

    public void register(String beanName, Class<?> beanClass) {
//        JavasistUtils.addAnnotationToClass(beanClass, Configurable.class, null);

        if (!genericApplicationContext.containsBeanDefinition(beanName) && !genericApplicationContext.containsBean(beanName)) {
            BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
            genericApplicationContext.registerBeanDefinition(beanName, beanDefinition);

            if (beanFactory instanceof BeanDefinitionRegistry) {
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                ConfigurationClassPostProcessor configurationClassPostProcessor = beanFactory.getBean(AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME, ConfigurationClassPostProcessor.class);
                configurationClassPostProcessor.processConfigBeanDefinitions(registry);

                // 手动获取一下
                System.out.println(beanFactory.getBeanPostProcessorCount());
//                beanFactory.getBean(DynamicAspect.class);
                beanFactory.getBean(DynamicAnnotationBeanPostProcessor.class);
                System.out.println(beanFactory.getBeanPostProcessorCount());
            }
        }

//        reader.register(beanClass);
        // 动态 识别 @Bean
//        ConfigurationClassPostProcessor configurationClassPostProcessor = beanFactory.getBean(AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME, ConfigurationClassPostProcessor.class);
//        configurationClassPostProcessor.processConfigBeanDefinitions(registry);

//        BeanDefinitionReaderUtils.
//        AnnotatedBeanDefinition

//        genericApplicationContext.registerBean(beanClass);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        if (beanFactory instanceof BeanDefinitionRegistry) {
            this.registry = (BeanDefinitionRegistry) beanFactory;
            this.reader = new AnnotatedBeanDefinitionReader(registry);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof GenericApplicationContext) {
            this.genericApplicationContext = (GenericApplicationContext) applicationContext;
        }
    }
}
