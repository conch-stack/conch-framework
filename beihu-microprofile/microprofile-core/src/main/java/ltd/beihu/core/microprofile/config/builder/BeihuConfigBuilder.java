package ltd.beihu.core.microprofile.config.builder;

import ltd.beihu.core.microprofile.config.BeihuConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import javax.annotation.Priority;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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



        return new BeihuConfig(sources, configConverters);
    }

    // private

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
