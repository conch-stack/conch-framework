package com.nabob.conch.sample.bootenhance.cbeanInstantiation;

import com.nabob.conch.sample.User;
import com.nabob.conch.sample.advice.agent.AgentMethodInterceptorAdvice;
import com.nabob.conch.sample.bootenhance.zdomain.SuperUser;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.util.ObjectUtils;

/**
 * 可直接用 @Bean进行注册；
 * 可以使用 configurableListableBeanFactory.addBeanPostProcessor(); {@linkplain  com.nabob.conch.sample.bootenhance.bbeanfactory.MyBeanFactoryPostProcessor }
 *
 * 调用顺序：
 * 1. postProcessBeforeInstantiation
 * 2. postProcessAfterInstantiation
 *
 * 3. postProcessProperties : postProcessAfterInstantiation=true
 *
 * 4. postProcessBeforeInitialization
 *      - AbstractAdvisingBeanPostProcessor
 * 5. postProcessAfterInitialization
 *
 */
public class MyInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    /**
     *
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 1. 会触发SpringIOC查找Advisor
     * wrapIfNecessary#getAdvicesAndAdvisorsForBean：Create proxy if we have advice. 表示这个方法会扫描所有advice
     * 并创建代理 {@linkplain org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator}
     *
     * 2.使用Spring AOP的扩展 加入Advice 切面：
     *  ProxyProcessorSupport 中设置了 该 BeanPostProcessor的优先级最低
     * {@linkplain org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor}
     * {@linkplain org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor}
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 会打破Spring的注册
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
//        if (ObjectUtils.nullSafeEquals("superUser", beanName) && SuperUser.class.equals(beanClass)) {
//            // 覆盖 superUser Bean
//            SuperUser superUser = new SuperUser();
//            superUser.setName("大飞飞");
//            superUser.setAge(18);
//            superUser.setAddress("北京");
//            // 会打破Spring的注册 【该bean之后的所有Bean】的后续流程将无法执行 postProcessAfterInstantiation等； 所以不要干这个事
//            return superUser;
//        }
        // 保持Spring IOC 默认容器实例化
        return null;
    }

    /**
     * 返回 false 则不会 对bean进行初始化操作（赋值）
     *
     * true: User{name='原始name', age=1000}  注册bean的时候的样子
     * false:
     */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (ObjectUtils.nullSafeEquals("user", beanName) && User.class.equals(bean.getClass())) {
            // 不赋值
            return false;
        }
        return true;
    }

    /**
     * 初始化后，可以 在这里动态的修改属性值
     */
    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if (ObjectUtils.nullSafeEquals("superUser", beanName) && SuperUser.class.equals(bean.getClass())) {
            // 可回调处理属性赋值 在 Populate Bean 阶段
            if (pvs instanceof MutablePropertyValues) {
                MutablePropertyValues mutablePropertyValues = (MutablePropertyValues) pvs;
                //                mutablePropertyValues.addPropertyValue("name", "test");
                // PropertyValue 对象中持有的对象是 final 的   | propertyValue 默认是 TypedStringValue
//                mutablePropertyValues.removePropertyValue("age");
                mutablePropertyValues.addPropertyValue("address", "非洲");
            }
        }
        return pvs;
    }
}