package com.nabob.conch.sample.advice.agentv2;

import com.nabob.conch.sample.advice.EnableRpcLogV2;
import com.nabob.conch.sample.advice.agentv2.processor.AgentPackageBeanPostProcessor;
import com.nabob.conch.sample.advice.agentv2.processor.RpcLogAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * RpcLogConfiguration
 *
 * @author Adam
 * @since 2023/3/15
 */
public class RpcLogConfiguration extends AbstractRpcConfigurationConfiguration {

    @Bean
    public RpcLogAnnotationBeanPostProcessor rpcLogAdvisor() {
        Assert.notNull(this.enableRpcLogV2, "@EnableRpcLogV2 annotation metadata was not injected");
        RpcLogAnnotationBeanPostProcessor bpp = new RpcLogAnnotationBeanPostProcessor(this.enableRpcLogV2);
        // 支持 指定注解类 以替换 默认注解类
        Class<? extends Annotation> customAsyncAnnotation = this.enableRpcLogV2.getClass("annotation");
        if (customAsyncAnnotation != AnnotationUtils.getDefaultValue(EnableRpcLogV2.class, "annotation")) {
            bpp.setSelfAnnotationType(customAsyncAnnotation);
        }
        bpp.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bpp;
    }

    @Bean
    public AgentPackageBeanPostProcessor agentPackageAdvisor() {
        Assert.notNull(this.enableRpcLogV2, "@EnableRpcLogV2 annotation metadata was not injected");
        AgentPackageBeanPostProcessor bpp = new AgentPackageBeanPostProcessor(this.enableRpcLogV2);
        bpp.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bpp;
    }

}
