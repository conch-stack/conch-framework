package com.nabob.conch.spring.lifecycle.beanlifecycle;

import com.nabob.conch.spring.ioc.overview.domain.SuperUser;
import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.util.ObjectUtils;

/**
 * Bean 实例化 前
 *
 * @author Adam
 * @date 2020/5/6
 */
public class BeanInstantiationLifecycleDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:/META-INF/dependency-lookup-context.xml");
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcessor());

        User user = beanFactory.getBean("user", User.class);
        System.out.println(user);

        User superUser = beanFactory.getBean("superUser", User.class);
        System.out.println(superUser);

    }

    static class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals("superUser", beanName) && SuperUser.class.equals(beanClass)) {
                // 覆盖 superUser Bean
                return new SuperUser();
            }
            // 保持Spring IOC 默认容器实例化
            return null;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
//            if (ObjectUtils.nullSafeEquals("user", beanName) && User.class.equals(bean.getClass())) {
//                // 不赋值
//                return false;
//            }
            return true;
        }

        @Override
        public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
            // 可回调处理属性赋值 在 Populate Bean 阶段
            if (pvs instanceof MutablePropertyValues) {
                MutablePropertyValues mutablePropertyValues = (MutablePropertyValues) pvs;
//                mutablePropertyValues.addPropertyValue("name", "test");
                // PropertyValue 对象中持有的对象是 final 的   | propertyValue 默认是 TypedStringValue
//                mutablePropertyValues.removePropertyValue("age");
                mutablePropertyValues.addPropertyValue("age", "11");
            }
            return pvs;
        }
    }

}
