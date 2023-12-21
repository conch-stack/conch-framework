package com.nabob.conch.agent.attach.spring.agent;

import com.nabob.conch.agent.attach.spring.support.zookeeper.SpringBeanUtil;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;

/**
 * agentmain 在 main 函数开始运行后才启动（依赖于Attach机制）
 */
public class MyAgent {

    /**
     * Classloader分析：
     * <p>
     * Thread ClassLoader:  AppClassLoader
     * MyAgent ClassLoader:  AppClassLoader
     * <p>
     * 目标程序SpringBean的Classloader: LaunchedURLClassLoader
     *
     * LaunchedURLClassLoader 是SpringBoot维护的ClassLoader，不在项目源码中，打包的时候被Spring利用FatJar方式写入的
     * SpringBoot项目的编译后文件：
     * - BOOT-INF: 存放lib包 + 项目代码
     * - META-INF: 存放配置文件
     *      - MANIFEST.MF
     * - org: 存放 Spring Boot Loader 相关源码
     *
     * <pre>
     *      MANIFEST.MF:
     *          Manifest-Version: 1.0
     *          Spring-Boot-Classpath-Index: BOOT-INF/classpath.idx
     *          Archiver-Version: Plexus Archiver
     *          Built-By: jz.zheng
     *          Start-Class: com.nabob.conch.agent.test.ConchAgentTestApplication
     *          Spring-Boot-Classes: BOOT-INF/classes/
     *          Spring-Boot-Lib: BOOT-INF/lib/
     *          Spring-Boot-Version: 2.3.10.RELEASE
     *          Created-By: Apache Maven 3.6.3
     *          Build-Jdk: 1.8.0_392
     *          Main-Class: org.springframework.boot.loader.JarLauncher
     * </pre>
     *
     * <pre>
     * Launcher -> ExecutableArchiveLauncher
     *              -> JarLauncher
     *              -> WarLauncher
     * </pre>
     */
    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("agentmain----attach----start");
        System.out.println("agentmain getPid = " + getPid());
        System.out.println("agentmain----attach----end");

        // 目前 Attach 失败， 初步猜测是 因为Attach的ClassLoader与目标JVM的Spring容器不是一个Loader
//        Object bean = SpringBeanUtil.getBean("dynamicService");
//        System.out.println(bean.toString());
//        System.out.println(bean.getClass().getSimpleName());

        // 打印 Classloader，发现是 使用的 AppClassLoader
        // Thread ClassLoader: sun.misc.Launcher$AppClassLoader@5c647e05
        // MyAgent ClassLoader: sun.misc.Launcher$AppClassLoader@5c647e05
        printClassLoader();


        ClassLoader classLoader = null;

        // 先学习 Instrumentation 吧
        Class<?>[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class<?> allLoadedClass : allLoadedClasses) {
            if (allLoadedClass.getName().contains("com.nabob")) {
                // com.nabob.conch.agent.test.ConchAgentTestApplication org.springframework.boot.loader.LaunchedURLClassLoader@2e5d6d97
                System.out.println(allLoadedClass.getName() + " " + allLoadedClass.getClassLoader().toString());
                if (classLoader == null) {
                    classLoader = allLoadedClass.getClassLoader();
                }
            }
        }

        try {
            Class<?> aClass = classLoader.loadClass("com.nabob.conch.agent.attach.spring.support.zookeeper.SpringBeanUtil");
            Object o = aClass.getDeclaredConstructor().newInstance();
            if (o instanceof SpringBeanUtil) {
                System.out.println("nice");
            } else {
                // 走到这了，还是那个问题，子ClassLoader加载的类，不能被父获取到
                System.out.println("bad");
                System.out.println(o.getClass());
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // TODO 所以，要想实现动态替换，可以考虑 一方面在目标项目中想办法引入钩子；一方面在Agent中注入字节码？ 最后还是回到 Instrumentation 上
    }

    static void printClassLoader() {
        ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
        if (threadClassLoader != null) {
            System.out.println("Thread ClassLoader: " + threadClassLoader.toString());
        } else {
            System.out.println("Thread ClassLoader: null");
        }

        ClassLoader myAgentClassLoader = MyAgent.class.getClassLoader();
        if (myAgentClassLoader != null) {
            System.out.println("MyAgent ClassLoader: " + myAgentClassLoader.toString());
        } else {
            System.out.println("MyAgent ClassLoader: null");
        }
    }

    private static String getPid() {
        String[] name = ManagementFactory.getRuntimeMXBean().getName().split("@");
        if (name.length < 1) {
            return "";
        }
        return name[0];
    }
}