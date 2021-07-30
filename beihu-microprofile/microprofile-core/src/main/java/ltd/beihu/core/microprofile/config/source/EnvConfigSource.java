package ltd.beihu.core.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * System Env ConfigSource
 *
 * @author Adam
 * @since 2021/7/29
 */
public class EnvConfigSource implements ConfigSource, Serializable {

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(System.getenv());
    }

    @Override
    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(System.getenv().keySet());
    }

    @Override
    public String getValue(String propertyName) {
        return System.getenv(propertyName);
    }

    @Override
    public int getOrdinal() {
        return 300;
    }

    @Override
    public String getName() {
        return "EnvConfigSource";
    }
}
