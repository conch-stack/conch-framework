package ltd.beihu.core.microprofile.config.builder;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Beihu ConfigBuilder
 *
 * @author Adam
 * @since 2021/7/29
 */
public class BeihuConfigBuilder implements ConfigBuilder {

    private boolean addDefaultSources = false;
    private boolean addDiscoveredSources = false;
    private boolean addDiscoveredConverters = false;

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    // sources are not sorted by their ordinals
    private List<ConfigSource> sources = new ArrayList<>();

    @Override
    public ConfigBuilder addDefaultSources() {
        addDefaultSources = true;
        return this;
    }

    @Override
    public ConfigBuilder addDiscoveredSources() {
        addDiscoveredSources = true;
        return this;
    }

    @Override
    public ConfigBuilder addDiscoveredConverters() {
        addDiscoveredConverters = true;
        return this;
    }

    @Override
    public ConfigBuilder forClassLoader(ClassLoader loader) {
        this.classLoader = loader;
        return this;
    }

    @Override
    public ConfigBuilder withSources(ConfigSource... sources) {
        for (ConfigSource source : sources) {
            this.sources.add(source);
        }
        return this;
    }

    @Override
    public ConfigBuilder withConverters(Converter<?>... converters) {

        for (Converter<?> converter : converters) {

        }

        return null;
    }

    @Override
    public <T> ConfigBuilder withConverter(Class<T> type, int priority, Converter<T> converter) {
        return null;
    }

    @Override
    public Config build() {
        return null;
    }
}
