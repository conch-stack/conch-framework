package com.scorer.boot.dynamic.load.jar.demo4;

import com.nabob.conch.dynamic.jar.DirectDalClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam
 * @since 2024/12/18
 */
public class DCacheProxy extends DirectDalClient {

    private static Map<String, ClassLoader> map = new HashMap<String, ClassLoader>();

    private String current;

    String s = "com.nabob.conch.dynamic.jar.DCacheClient";

    @Override
    public void test() {
        if (current == null) {
            System.out.println("请先加载Jar");
            return;
        }

        ClassLoader classLoader = map.get(current);
        try {
            Class<?> aClass1 = classLoader.loadClass(s);
            Object o = aClass1.getDeclaredConstructor().newInstance();
            aClass1.getMethod("test").invoke(o);
        } catch (Throwable e) {

        }
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public void put(String key, ClassLoader classLoader) {
        map.put(key, classLoader);
    }
}
