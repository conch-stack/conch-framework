package com.nabob.conch.sample.dynamic.aop;

import com.nabob.conch.sample.advice.agentv2.advisor.RpcLogAnnotationAdvisor;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * 自定义 注解 接入 AOP
 *
 * @author Adam
 * @since 2023/3/15
 */
public class DynamicAnnotationBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

    public DynamicAnnotationBeanPostProcessor() {
        // 在存在 Advisors 之前进行设置
//        setBeforeExistingAdvisors(true);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        DynamicAnnotationAdvisor advisor = new DynamicAnnotationAdvisor();
        advisor.setBeanFactory(beanFactory);
        this.advisor = advisor;
    }
}
