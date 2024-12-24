package com.scorer.boot.dynamic.load.jar.demo4;

import com.nabob.conch.dynamic.jar.DirectDalClient;
import com.scorer.boot.dynamic.load.jar.demo2.jar.JarFile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author Adam
 * @since 2024/12/18
 */
@Service
public class DCacheLoader {

    // https://blog.51cto.com/u_16213438/9073972
    String s = "com.nabob.conch.dynamic.jar.DCacheClient";

    public ClassLoader getClassLoader(File file) throws Throwable {
        JarFile jarFile1 = new JarFile(file);
        URL[] url1 = {jarFile1.getUrl()};
        return new DCacheClassLoader(url1);
    }

    public void load() throws Throwable {

        File file2 = new File("D:\\tmp\\conch-dynamic-jar-0.0.1.jar");
        File file1 = new File("D:\\tmp\\conch-dynamic-jar-0.0.2.jar");

        JarFile jarFile1 = new JarFile(file1);
        JarFile jarFile2 = new JarFile(file2);

        URL[] url1 = {jarFile1.getUrl()};
        URL[] url2 = new URL[]{jarFile2.getUrl()};

        DCacheClassLoader loader1 = new DCacheClassLoader(url1);
        Class<?> aClass1 = loader1.loadClass(s);
//        DirectDalClient t1 = (DirectDalClient) aClass1.getDeclaredConstructor().newInstance();
//        t1.test();
        Object o1 = aClass1.getDeclaredConstructor().newInstance();
        Method test1 = aClass1.getMethod("test");
        test1.invoke(o1);

        DCacheClassLoader loader2 = new DCacheClassLoader(url2);
        Class<?> aClass2 = loader2.loadClass(s);
//        DirectDalClient t2 = (DirectDalClient) aClass2.getDeclaredConstructor().newInstance();
//        t2.test();
        Object o2 = aClass2.getDeclaredConstructor().newInstance();
        Method test2 = aClass2.getMethod("test");
        test2.invoke(o2);
    }
}
