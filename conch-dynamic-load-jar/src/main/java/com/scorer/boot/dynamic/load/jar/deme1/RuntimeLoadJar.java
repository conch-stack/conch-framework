package com.scorer.boot.dynamic.load.jar.deme1;

import javax.annotation.Resource;

/**
 * 但是由于没有对插件之间的 ClassLoader 进行 「隔离」 也可能会存在如类冲突、版本冲突等问题；
 * 并且由于 ClassLoader 中的 Class 对象无法销毁，所以除非修改类名或者类路径，
 * 不然插件中已加载到 ClassLoader 的类是没办法动态修改的。
 *
 * @author Adam
 * @since 2024/12/16
 */
public class RuntimeLoadJar {

    @Resource
    private SpringUtil springUtil;

//    @GetMapping("/reload")
    public Object reload(String targetUrl) throws ClassNotFoundException {
        ClassLoader classLoader = ClassLoaderUtil.getClassLoader(targetUrl);
        Class<?> clazz = classLoader.loadClass(Plugin.pluginClass);
        springUtil.registerBean(clazz.getName(), clazz);
        Plugin plugin = (Plugin)springUtil.getBean(clazz.getName());
        return plugin.sayHello("test reload");
    }
}
