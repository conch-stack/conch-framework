package ltd.beihu.core.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

/**
 * Beihu ConfigProviderResolver
 *
 * @author Adam
 * @since 2021/7/29
 */
public class BeihuConfigProviderResolver extends ConfigProviderResolver {

    @Override
    public Config getConfig() {
        return null;
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        return null;
    }

    @Override
    public ConfigBuilder getBuilder() {
        return null;
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {

    }

    @Override
    public void releaseConfig(Config config) {

    }
}
