package com.nabob.conch.dubbo.server.spi;

import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * Dubbo Service Provider Interface
 *
 *      核心类：ExtensionLoader
 *      Dubbo SPI 除了支持按需加载接口实现类，还增加了 IOC 和 AOP 等特性
 *
 * @author Adam
 * @date 2021/4/7
 */
public class DubboSpiTest {

    public static void main(String[] args) {

        ExtensionLoader<Robot> extensionLoader = ExtensionLoader.getExtensionLoader(Robot.class);

        Robot optimusPrime = extensionLoader.getExtension("optimusPrime");
        optimusPrime.sayHello();

        Robot bumblebee = extensionLoader.getExtension("bumblebee");
        bumblebee.sayHello();
    }

}
