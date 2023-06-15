package com.nabob.conch.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Properties ConfigSourceProvider
 *
 * @author Adam
 * @since 2021/7/29
 */
public class PropertiesConfigSourceProvider implements ConfigSourceProvider {

    private List<ConfigSource> configSources = new ArrayList<>();

    public PropertiesConfigSourceProvider(String propertyFileName, boolean optional, ClassLoader classLoader) {
        try {
            Enumeration<URL> propertyFileUrls = classLoader.getResources(propertyFileName);

            if (!optional && !propertyFileUrls.hasMoreElements()) {
                throw new IllegalStateException(propertyFileName + " wasn't found.");
            }

            while (propertyFileUrls.hasMoreElements()) {
                URL propertyFileUrl = propertyFileUrls.nextElement();
                configSources.add(new PropertiesConfigSource(propertyFileUrl));
            }
        }
        catch (IOException ioe) {
            throw new IllegalStateException("problem while loading microprofile-config.properties files", ioe);
        }

    }

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        return configSources;
    }
}
