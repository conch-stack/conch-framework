package com.nabob.conch.sample.bootenhance.bbeanfactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * ScanConfigurationClassPostProcessor
 *
 * {@link org.springframework.context.annotation.ComponentScans}  处理类 ComponentScanAnnotationParser
 *
 * @author Adam
 * @since 2024/8/13
 */
public class ScanConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered, ResourceLoaderAware, EnvironmentAware {

    private String basePackage = "com.nabob.conch.sample";

    private Environment environment;
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private BeanNameGenerator componentScanBeanNameGenerator = new AnnotationBeanNameGenerator(); // spring 5.2后 AnnotationBeanNameGenerator.INSTANCE;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        scanner.setBeanNameGenerator(componentScanBeanNameGenerator);
        scanner.setEnvironment(environment);
        scanner.setResourceLoader(resourceLoader);

        scanner.scan(basePackage);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        // do nothing
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }
}
