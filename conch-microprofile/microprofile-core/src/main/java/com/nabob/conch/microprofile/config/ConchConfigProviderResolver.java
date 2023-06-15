package com.nabob.conch.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Beihu ConfigProviderResolver
 *
 * @author Adam
 * @since 2021/7/29
 */
public class BeihuConfigProviderResolver extends ConfigProviderResolver {

    private final Map<ClassLoader, Config> configForClassLoaderMap = new HashMap<>();

    @Override
    public Config getConfig() {
        return getConfig(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        Config config = configForClassLoaderMap.get(loader);
        if (Objects.nonNull(config)) {
            return config;
        }

        config = getBuilder().forClassLoader(loader)
                .addDefaultSources()
                .addDiscoveredSources()
                .addDiscoveredConverters()
                .build();

        registerConfig(config, loader);
        return config;
    }

    @Override
    public ConfigBuilder getBuilder() {
        return new BeihuConfigBuilder();
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        configForClassLoaderMap.put(classLoader, config);
    }

    @Override
    public void releaseConfig(Config config) {
        Iterator<Map.Entry<ClassLoader, Config>> iterator = configForClassLoaderMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ClassLoader, Config> next = iterator.next();
            if (next.getValue() == config) {
                iterator.remove();
                return;
            }
        }
    }
}
