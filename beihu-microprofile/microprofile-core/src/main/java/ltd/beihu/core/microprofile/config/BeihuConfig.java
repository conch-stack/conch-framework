package ltd.beihu.core.microprofile.config;

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
import java.util.Objects;
import java.util.Optional;

/**
 * Baihu Config
 *
 * @author Adam
 * @since 2021/7/29
 */
public class BeihuConfig implements Config {

    private final List<ConfigSource> configSources;
    private Map<Type, Converter<?>> typeConverterMap;

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

            }
        }
        return null;
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        return null;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        return Optional.empty();
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return null;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return null;
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {

        Converter<?> converter = typeConverterMap.get(forType);

        return Optional.empty();
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

            Converter<?> converter = typeConverterMap.get(componentType);
            if (Objects.isNull(converter)) {
                return null;
            }

            String[] split = StringUtil.split(value);
            T rs = (T) Array.newInstance(componentType, split.length);
            for (int i = 0; i < split.length; i++) {
                converter.convert(split[i])
            }



        } else {

        }

    }
}
