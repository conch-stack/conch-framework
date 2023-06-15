package com.nabob.conch.sample.advice.agentv2.processor;

import com.nabob.conch.sample.advice.agentv2.advisor.AgentPackageAdvisor;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.StringUtils;

/**
 * 自定义 注解 接入 AOP
 *
 * @author Adam
 * @since 2023/3/15
 */
public class AgentPackageBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

    /**
     * 可以转换成对应的ConfigBean，我这里就简单使用一下了
     */
    protected AnnotationAttributes enableRpcLogV2;

    public AgentPackageBeanPostProcessor(AnnotationAttributes enableRpcLogV2) {
        // 在存在 Advisors 之前进行设置
        setBeforeExistingAdvisors(true);
        this.enableRpcLogV2 = enableRpcLogV2;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);

        // 必须配置了
        String agentPackage = this.enableRpcLogV2.getString("agentPackage");
        if (StringUtils.isEmpty(agentPackage)) {
            return;
        }

        AgentPackageAdvisor advisor = new AgentPackageAdvisor(this.enableRpcLogV2);
        advisor.setBeanFactory(beanFactory);
        this.advisor = advisor;
    }
}
