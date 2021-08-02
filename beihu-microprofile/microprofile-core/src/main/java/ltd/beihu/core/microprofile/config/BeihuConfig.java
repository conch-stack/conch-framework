package ltd.beihu.core.microprofile.config;

import ltd.beihu.core.microprofile.config.converter.Converters;
import ltd.beihu.core.microprofile.config.converter.ImplicitConverters;
import ltd.beihu.core.microprofile.config.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Baihu Config
 *
 * @author Adam
 * @since 2021/7/29
 */
public class BeihuConfig implements Config {

    private final List<ConfigSource> configSources;
    private final Map<Type, Converter<?>> typeConverterMap;

    BeihuConfig(List<ConfigSource> configSources, Map<Type, Converter<?>> typeConverterMap) {
        this.configSources = CollectionUtils.isNotEmpty(configSources) ? configSources : new ArrayList<>();
        this.typeConverterMap = new HashMap<>(Converters.ALL_CONVERTERS);
        this.typeConverterMap.putAll(typeConverterMap);
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        for (ConfigSource configSource : configSources) {
            String value = configSource.getValue(propertyName);
            if (value != null && value.length() > 0) {
                return convert(value, propertyType);
            }
        }
        return null;
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        // todo impl
        return null;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        for (ConfigSource configSource : configSources) {
            String value = configSource.getValue(propertyName);
            if (value != null && value.length() > 0) {
                return Optional.of(convert(value, propertyType));
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return configSources.stream().flatMap(a -> a.getPropertyNames().stream()).collect(Collectors.toSet());
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return configSources;
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        Converter<T> converter = doGetConverter(forType);
        return Optional.of(converter);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }

    // private

    private <T> T convert(String value, Class<T> propertyType) {
        if (value == null || value.length() <= 0) {
            return null;
        }

        // 数组 特殊处理
        if (propertyType.isArray()) {
            // 真正需要处理的 Type
            Class<?> componentType = propertyType.getComponentType();

            Converter<T> converter = doGetConverter(componentType);

            String[] split = StringUtil.split(value);
            T rs = (T) Array.newInstance(componentType, split.length);
            for (int i = 0; i < split.length; i++) {
                T t = converter.convert(split[i]);
                Array.set(rs, i, t);
            }
            return rs;
        } else {
            Converter<T> converter = doGetConverter(propertyType);
            return converter.convert(value);
        }
    }

    private <T> Converter doGetConverter(Class<T> forType) {
        if (forType.isArray()) {
            return doGetConverter(forType.getComponentType());
        } else {
            Converter converter = typeConverterMap.get(forType);
            if (converter == null) {
                // look for implicit converters
                synchronized (typeConverterMap) {
                    converter = ImplicitConverters.getConverter(forType);
                    typeConverterMap.putIfAbsent(forType, converter);
                }
            }
            if (converter == null) {
                throw new IllegalArgumentException("No Converter registered for class " + forType);
            }
            return converter;
        }
    }
}
