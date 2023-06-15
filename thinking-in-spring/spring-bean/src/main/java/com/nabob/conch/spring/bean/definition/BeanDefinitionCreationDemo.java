package com.nabob.conch.spring.bean.definition;

import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * 创建 BeanDefinition
 *
 * {@link org.springframework.beans.factory.config.BeanDefinition}
 *
 * @author Adam
 * @since 2020/3/31
 */
public class BeanDefinitionCreationDemo {

    public static void main(String[] args) {
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
