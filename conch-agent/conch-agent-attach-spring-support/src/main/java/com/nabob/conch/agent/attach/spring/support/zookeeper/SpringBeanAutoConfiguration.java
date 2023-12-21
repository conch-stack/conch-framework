package com.nabob.conch.agent.attach.spring.support.zookeeper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeanAutoConfiguration {

    /**
     * Spring Bean support
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringBeanUtil springBeanUtil() {
        return new SpringBeanUtil();
    }

}
