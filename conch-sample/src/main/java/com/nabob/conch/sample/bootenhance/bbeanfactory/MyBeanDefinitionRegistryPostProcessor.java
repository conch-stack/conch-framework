package com.nabob.conch.sample.bootenhance.bbeanfactory;

import com.nabob.conch.sample.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * 时机: refresh()的 this.invokeBeanFactoryPostProcessors(beanFactory);此时此bean的定义信息 都已经加载完毕 但是还没到实例化以及初始化阶段
 *
 * @author Adam
 * @since 2024/8/8
 */
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        System.out.println("-----------------------[BeanDefinitionRegistryPostProcessor扩展点演示] # postProcessBeanDefinitionRegistry 开始--------------------------------------");
        System.out.println("[BeanDefinitionRegistryPostProcessor扩展点演示] # postProcessBeanDefinitionRegistry");
        System.out.println("时机: refresh()的 this.invokeBeanFactoryPostProcessors(beanFactory); 方法中执行; " +
                "此时 bean的定义信息 都已经加载完毕 但是还没到实例化以及初始化阶段");
        System.out.println("可以对beanDefinitionRegistry进行操作，比如：beanDefinitionRegistry.registerBeanDefinition()");
        System.out.println("-----------------------[BeanDefinitionRegistryPostProcessor扩展点演示] # postProcessBeanDefinitionRegistry 结束--------------------------------------");
        System.out.println();

        beanDefinitionRegistry.registerBeanDefinition("BeanDefinitionRegistryUser", buildBeanDefinitionBuild());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        System.out.println("==================这是我自定义的BeanDefinitionRegistryPostProcessor====================");
        System.out.println("可以对 BeanFactory（ConfigurableListableBeanFactory） 进行操作");
    }

    public BeanDefinition buildBeanDefinitionBuild() {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        beanDefinitionBuilder.addPropertyValue("name", "啧啧啧");
        beanDefinitionBuilder.addPropertyValue("age", 10);
        return beanDefinitionBuilder.getBeanDefinition();
    }

    public void testBeanDefinitionBuild() {
        // 1. 通过 BeanDefinitionBuilder 构建
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        beanDefinitionBuilder.addPropertyValue("name", "啧啧啧");
        beanDefinitionBuilder.addPropertyValue("age", 10);
        // 获取 BeanDefinition
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        // BeanDefinition 本非 Bean的最终状态，还可以进行修改
//        beanDefinition.setAutowireMode();


        // 2. 通过 AbstractBeanDefinition 构建
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(User.class);
        MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
        mutablePropertyValues.addPropertyValue("name", "啧啧啧");
        mutablePropertyValues.addPropertyValue("age", 12);
        genericBeanDefinition.setPropertyValues(mutablePropertyValues);

        // 3. AnnotatedBeanDefinition
    }

}
