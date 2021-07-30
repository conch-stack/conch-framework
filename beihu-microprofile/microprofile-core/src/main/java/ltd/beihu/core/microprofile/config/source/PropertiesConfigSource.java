package ltd.beihu.core.microprofile.config.source;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Properties ConfigSource
 *
 * @author Adam
 * @since 2021/7/29
 */
@SuppressWarnings("all")
public class PropertiesConfigSource implements ConfigSource, Serializable {

    private final Map<String, String> properties;
    private final String source;
    private final int ordinal;

    public PropertiesConfigSource(URL url) throws IOException {
        this.source = url.toString();

        try (InputStream in = url.openStream()) {
            Properties p = new Properties();
            p.load(in);

            this.properties = new HashMap(p);
        }

        final String ordinalConfig = this.properties.get(CONFIG_ORDINAL);
        this.ordinal = StringUtils.isBlank(ordinalConfig) ? DEFAULT_ORDINAL : Integer.parseInt(ordinalConfig);
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(this.properties.keySet());
    }

    @Override
    public String getValue(String propertyName) {
        return this.properties.get(propertyName);
    }

    @Override
    public int getOrdinal() {
        return this.ordinal;
    }

    @Override
    public String getName() {
        return "PropertiesConfigSource";
    }
}
