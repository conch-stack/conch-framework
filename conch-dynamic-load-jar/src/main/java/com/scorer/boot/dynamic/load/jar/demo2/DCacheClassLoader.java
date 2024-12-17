package com.scorer.boot.dynamic.load.jar.demo2;

import com.google.common.cache.Cache;
import com.scorer.boot.dynamic.load.jar.demo2.jar.Handler;
import com.scorer.boot.dynamic.load.jar.demo2.util.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Adam
 * @since 2024/12/17
 */
public class DCacheClassLoader extends URLClassLoader {

    /**
     * The parent class loader.
     */
    protected final ClassLoader parent;

    /**
     * The bootstrap class loader used to load the JavaSE classes. In some
     * implementations this class loader is always <code>null</code> and in
     * those cases {@link ClassLoader#getParent()} will be called recursively on
     * the system class loader and the last non-null result used.
     */
    private ClassLoader javaseClassLoader;

    protected Cache<String, LoadClassResult> classCache;

    private static final List<String> SUN_REFLECT_GENERATED_ACCESSOR            = new ArrayList<>();

    static {
        ClassLoader.registerAsParallelCapable();
        SUN_REFLECT_GENERATED_ACCESSOR.add("sun.reflect.GeneratedMethodAccessor");
        SUN_REFLECT_GENERATED_ACCESSOR.add("sun.reflect.GeneratedConstructorAccessor");
        SUN_REFLECT_GENERATED_ACCESSOR.add("sun.reflect.GeneratedSerializationConstructorAccessor");
    }

    public boolean isSunReflectClass(String className) {
        for (String sunAccessor : SUN_REFLECT_GENERATED_ACCESSOR) {
            if (className.startsWith(sunAccessor)) {
                return true;
            }
        }
        return false;
    }

    public DCacheClassLoader() {
        super(new URL[0]);

        ClassLoader p = getParent();
        if (p == null) {
            p = getSystemClassLoader();
        }
        this.parent = p;

        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = getSystemClassLoader();
            // 获取 ExtClassLoader
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        // 这就是一个 ExtClassLoader

        List<URL> jdkUrls = new ArrayList<>();
        try {
            String javaHome = System.getProperty("java.home").replace(File.separator + "jre", "");
            URL[] urls = getURLs(ClassLoader.getSystemClassLoader());
            for (URL url : urls) {
                if (url.getPath().startsWith(javaHome)) {
                    jdkUrls.add(url);
                }
            }
        } catch (Throwable e) {
           e.printStackTrace();
        }

        this.javaseClassLoader = new JDKDelegateClassLoader(jdkUrls.toArray(new URL[0]), j);
    }

    protected ClassLoader getJavaseClassLoader() {
        return javaseClassLoader;
    }

    @SuppressWarnings({ "restriction", "unchecked" })
    public static URL[] getURLs(ClassLoader classLoader) {
        // https://stackoverflow.com/questions/46519092/how-to-get-all-jars-loaded-by-a-java-application-in-java9
        if (classLoader instanceof URLClassLoader) {
            return ((URLClassLoader) classLoader).getURLs();
        }

        // support jdk9+
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(System.getProperty("path.separator"));
        List<URL> classpathURLs = new ArrayList<>();
        for (String classpathEntry : classpathEntries) {
            URL url = null;
            try {
                url = FileUtils.file(classpathEntry).toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to get urls from " + classLoader, e);
            }
            classpathURLs.add(url);
        }

        return classpathURLs.toArray(new URL[0]);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Handler.setUseFastConnectionExceptions(true);
        try {
            return loadClassWithCache(name, resolve);
        } finally {
            Handler.setUseFastConnectionExceptions(false);
        }
    }

    private Class<?> loadClassWithCache(String name, boolean resolve) {
        try {
            LoadClassResult resultInCache = classCache.get(name, () -> {
                LoadClassResult r = new LoadClassResult();
                try {
                    r.setClazz(loadClassInternal(name, resolve));
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
                return r;
            });

            return resultInCache.getClazz();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Class loadClassInternal(String name, boolean resolve) {
        Class<?> clazz = null;

        // 0. sun reflect related class throw exception directly
        if (isSunReflectClass(name)) {
            throw new RuntimeException(String.format("[ArkBiz Loader] %s : can not load class: %s, this class can only be loaded by sun.reflect.DelegatingClassLoader", "DCache", name));
        }

        // 1. findLoadedClass
        if (clazz == null) {
            clazz = findLoadedClass(name);
        }

        // 2. JDK related class
        if (clazz == null) {
            clazz = resolveJDKClass(name);
        }

        // 6. Biz classpath class
        if (clazz == null) {
            clazz = resolveLocalClass(name);
        }

        if (clazz != null) {
            if (resolve) {
                super.resolveClass(clazz);
            }
            return clazz;
        }

        throw new RuntimeException(String.format("[ArkBiz Loader] %s : can not load class: %s",
                "DCache", name));
    }

    /**
     * Load JDK class
     * @param name class name
     * @return
     */
    protected Class<?> resolveJDKClass(String name) {
        try {
            return getJavaseClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            // ignore
        }
        return null;
    }

    /**
     * Load classpath class
     * @param name
     * @return
     */
    protected Class<?> resolveLocalClass(String name) {
        try {
            return super.loadClass(name, false);
        } catch (ClassNotFoundException e) {
            // ignore
        }
        return null;
    }

    public static class LoadClassResult {
        private Class              clazz;

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }
    }
}
