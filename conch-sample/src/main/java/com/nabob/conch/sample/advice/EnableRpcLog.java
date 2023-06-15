package com.nabob.conch.sample.advice;

import com.nabob.conch.sample.advice.agent.AgentBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启rpc记录日志功能
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(AgentBeanDefinitionRegistrar.class)
public @interface EnableRpcLog {
    @AliasFor("agentPackage")
    String value() default "";

    @AliasFor("value")
    String agentPackage() default "";

    /**
     * 是否记录ck日志，默认写入
     */
    boolean recordCk() default true;
}