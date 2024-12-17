package com.scorer.boot.dynamic.load.jar.deme1;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

@Slf4j
public class ClassLoaderUtil {

    /**
     * 通过指定的链接或者路径动态加载 jar 包，可以使用 URLClassLoader 的 addURL 方法来实现
     * <p>
     * 其中在创建 URLClassLoader 时，指定当前系统的 ClassLoader 为父类加载器  ClassLoader.getSystemClassLoader() 这步比较关键，用于打通主程序与插件之间的 ClassLoader ，解决把插件注册进 IOC 时的各种 ClassNotFoundException 问题
     */
    public static ClassLoader getClassLoader(String url) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            URLClassLoader classLoader = new URLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());
            method.invoke(classLoader, new URL(url));
            return classLoader;
        } catch (Exception e) {
            log.error("getClassLoader-error", e);
            return null;
        }
    }
}