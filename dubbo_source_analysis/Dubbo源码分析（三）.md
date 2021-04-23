## Dubbo源码分析（三）- SPI

Dubbo Service Provider Interface

- 核心类：ExtensionLoader
- 接口需添加@SPI注解
- Dubbo SPI 除了支持按需加载接口实现类，还增加了 IOC 和 AOP 等特性



##### 加载流程

![image-20210423135131097](assets/image-20210423135131097.png)



- Activate
- Wrapper



##### Dubbo IOC特性

Dubbo IOC 是通过 setter 方法注入依赖

Dubbo 首先会通过反射获取到实例的所有方法，然后再遍历方法列表，检测方法名是否具有 setter 方法特征。若有，则通过 ObjectFactory 获取依赖对象，最后通过反射调用 setter 方法将依赖设置到目标对象中。



objectFactory 变量的类型为 AdaptiveExtensionFactory，AdaptiveExtensionFactory 内部维护了一个 ExtensionFactory 列表，用于存储其他类型的 ExtensionFactory。Dubbo 目前提供了两种 ExtensionFactory，分别是 SpiExtensionFactory 和 SpringExtensionFactory。前者用于创建自适应的拓展，后者是用于从 Spring 的 IOC 容器中获取所需的拓展。