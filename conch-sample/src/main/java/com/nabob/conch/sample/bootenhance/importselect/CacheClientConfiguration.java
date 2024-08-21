package com.nabob.conch.sample.bootenhance.importselect;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * CacheClientConfiguration
 *
 * @author Adam
 * @since 2024/8/12
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CacheClientConfiguration extends AbstractCacheConfiguration {

    public CacheClientConfiguration() {
        System.out.println("CacheClientConfiguration");
    }

}
