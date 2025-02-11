package com.scorer.boot.dynamic.load.jar.deme1;

/**
 * @author Adam
 * @since 2024/12/16
 */
public interface Plugin {

    String pluginClass = "com.plugin.impl.PluginImpl";

    String sayHello(String info);
}
