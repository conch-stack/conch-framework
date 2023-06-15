package com.nabob.conch.microprofile.config;

import com.nabob.conch.microprofile.config.source.EnvConfigSource;
import com.nabob.conch.microprofile.config.source.PropertiesConfigSourceProvider;
import com.nabob.conch.microprofile.config.source.SysPropConfigSource;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.eclipse.microprofile.config.spi.Converter;

import javax.annotation.Priority;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Beihu ConfigBuilder
 *
 * @author Adam
 * @since 2021/7/29
 */
public class BeihuConfigBuilder implements ConfigBuilder {

    private static final String META_INF_MICROPROFILE_CONFIG_PROPERTIES = "META-INF/microprofile-config.properties";
    private static final String WEB_INF_MICROPROFILE_CONFIG_PROPERTIES = "WEB-INF/classes/META-INF/microprofile-config.properties";

    private boolean addDefaultSources = false;
    private boolean addDiscoveredSources = false;
    private boolean addDiscoveredConverters = false;

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    // sources are not sorted by their ordinals
    private List<ConfigSource> sources = new ArrayList<>();

    private Map<Type, ConverterWithPriority> converters = new HashMap<>();

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
            Type converterForType = getConverterForType(converter.getClass());
            if (Objects.isNull(converterForType)) {
                throw new IllegalStateException("Can not add converter " + converter + " that is not parameterized with a type");
            }
            addConverter(converterForType, getConverterPriority(converter), converter);
        }
        return this;
    }

    @Override
    public <T> ConfigBuilder withConverter(Class<T> type, int priority, Converter<T> converter) {
        addConverter(type, priority, converter);
        return this;
    }

    @Override
    public Config build() {
        if (addDefaultSources) {
            sources.addAll(getDefaultSources());
        }

        if (addDiscoveredSources) {
            sources.addAll(getDiscoveredSources());
        }

        if (addDiscoveredConverters) {
            for (Converter converter : getDiscoveredConverter()) {
                Type converterForType = getConverterForType(converter.getClass());
                if (Objects.isNull(converterForType)) {
                    throw new IllegalStateException("Can not add converter " + converter + " that is not parameterized with a type");
                }
                addConverter(converterForType, getConverterPriority(converter), converter);
            }
        }

        // 排序 - 倒序
        sources.sort((o1, o2) -> o2.getOrdinal() - o1.getOrdinal());

        // 转换converter
        Map<Type, Converter<?>> targetConverters = new HashMap<>();
        converters.forEach((key, value) -> targetConverters.put(key, value.converter));

        return new BeihuConfig(sources, targetConverters);
    }

    // private

    /**
     * 获取 SPI 配置的 Converter
     *
     * @return list of Converter
     */
    private List<Converter> getDiscoveredConverter() {
        List<Converter> rs = new ArrayList<>();
        ServiceLoader<Converter> converterServiceLoader = ServiceLoader.load(Converter.class, classLoader);
        converterServiceLoader.forEach(rs::add);
        return rs;
    }

    /**
     * 获取 SPI 配置的 Config Source
     *
     * @return list of spi discovered config source
     */
    private List<ConfigSource> getDiscoveredSources() {
        List<ConfigSource> rs = new ArrayList<>();
        ServiceLoader<ConfigSource> configSourcesServiceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
        configSourcesServiceLoader.forEach(rs::add);

        ServiceLoader<ConfigSourceProvider> configSourceProviderServiceLoader = ServiceLoader.load(ConfigSourceProvider.class, classLoader);
        configSourceProviderServiceLoader.forEach(configSourceProvider -> {
            Iterable<ConfigSource> configSources = configSourceProvider.getConfigSources(classLoader);
            configSources.forEach(rs::add);
        });
        return rs;
    }

    /**
     * 获取 Default Config Source
     *
     * @return list of default config source
     */
    private List<ConfigSource> getDefaultSources() {
        List<ConfigSource> rs = new ArrayList<>();
        rs.add(new EnvConfigSource());
        rs.add(new SysPropConfigSource());

        Iterable<ConfigSource> configSources1 = new PropertiesConfigSourceProvider(META_INF_MICROPROFILE_CONFIG_PROPERTIES, true, classLoader).getConfigSources(classLoader);
        configSources1.forEach(rs::add);
        Iterable<ConfigSource> configSources2 = new PropertiesConfigSourceProvider(WEB_INF_MICROPROFILE_CONFIG_PROPERTIES, true, classLoader).getConfigSources(classLoader);
        configSources2.forEach(rs::add);
        return rs;
    }

    /**
     * 添加 Converter
     *
     * @param type      converter for type
     * @param priority  converter priority
     * @param converter converter
     */
    private void addConverter(Type type, int priority, Converter<?> converter) {
        // add the converter only if it has a higher priority than another converter for the same type
        ConverterWithPriority oldConverter = this.converters.get(type);

        // add or replace
        if (Objects.isNull(oldConverter) || priority > oldConverter.priority) {
            this.converters.put(type, new ConverterWithPriority(converter, priority));
        }
    }

    /**
     * 获取 Converter 的 优先级
     * <p>
     * priority, Converter Annotated with {@link Priority} , default is {@link ConfigSource#DEFAULT_ORDINAL}
     *
     * @param converter converter
     * @return priority of converter
     */
    private int getConverterPriority(Converter<?> converter) {
        Priority annotation = converter.getClass().getAnnotation(Priority.class);
        if (Objects.nonNull(annotation)) {
            return annotation.value();
        }
        return ConfigSource.DEFAULT_ORDINAL;
    }

    /**
     * 递归 获取 Converter 的 For Type
     *
     * @param clazz target class
     * @return convertor for type
     */
    private Type getConverterForType(Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return null;
        }

        for (Type genericInterface : clazz.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) genericInterface;
                if (pType.getRawType().equals(Converter.class)) {
                    Type[] actualTypeArguments = pType.getActualTypeArguments();
                    if (actualTypeArguments.length != 1) {
                        throw new IllegalStateException("Converter " + clazz + " must be parameterized with a single type");
                    }
                    return actualTypeArguments[0];
                }
            }
        }

        return getConverterForType(clazz.getSuperclass());
    }

    /**
     * Converter with Priority
     */
    private static class ConverterWithPriority {

        private final Converter<?> converter;
        private final int priority;

        private ConverterWithPriority(Converter<?> converter, int priority) {
            this.converter = converter;
            this.priority = priority;
        }
    }
}
