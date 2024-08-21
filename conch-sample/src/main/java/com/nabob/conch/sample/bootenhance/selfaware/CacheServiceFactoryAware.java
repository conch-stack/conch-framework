package com.nabob.conch.sample.bootenhance.selfaware;

import org.springframework.beans.factory.Aware;

/**
 * CacheServiceFactoryAware
 *
 * @author Adam
 * @since 2024/8/12
 */
public interface CacheServiceFactoryAware extends Aware {

    void setCacheServiceFactory(CacheServiceFactory cacheServiceFactory);
}
