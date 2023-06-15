package com.nabob.conch.sample.advice.agentv2.processor;

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
public class RpcLogAnnotationBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

    /**
     * 可指定自定义的注解，替换默认的 @RpcLog 注解
     */
    @Nullable
    private Class<? extends Annotation> selfAnnotationType;

    /**
     * 可以转换成对应的ConfigBean，我这里就简单使用一下了
     */
    protected AnnotationAttributes enableRpcLogV2;

    public RpcLogAnnotationBeanPostProcessor(AnnotationAttributes enableRpcLogV2) {
        // 在存在 Advisors 之前进行设置
        setBeforeExistingAdvisors(true);
        this.enableRpcLogV2 = enableRpcLogV2;
    }

    public void setSelfAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
        Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
        this.selfAnnotationType = asyncAnnotationType;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);

        RpcLogAnnotationAdvisor advisor = new RpcLogAnnotationAdvisor(this.enableRpcLogV2);
        if (this.selfAnnotationType != null) {
            advisor.setSelfAnnotationType(this.selfAnnotationType);
        }
        advisor.setBeanFactory(beanFactory);
        this.advisor = advisor;
    }
}
