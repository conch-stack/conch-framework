package ltd.beihu.core.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * System Property ConfigSource
 *
 * @author Adam
 * @since 2021/7/29
 */
public class SysPropConfigSource implements ConfigSource, Serializable {

    @Override
    @SuppressWarnings("all")
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(new HashMap(System.getProperties()));
    }

    @Override
    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(System.getProperties().stringPropertyNames());
    }

    @Override
    public String getValue(String propertyName) {
        return System.getProperty(propertyName);
    }

    @Override
    public int getOrdinal() {
        return 400;
    }

    @Override
    public String getName() {
        return "SysPropConfigSource";
    }
}
