## SpringBoot自动配置流程（2.x）

> 核心：
>
> - SpringFactoriesLoader
> - AutoConfigurationGroup
>
> **SpringBoot3.x已废弃改模式，改用新的import方式，具体后续有时间再分享**



SpringBoot启动后，加载BeanPostProcessor

其中的ConfigurationClassPostProcessor会执行生命周期：postProcessBeanDefinitionRegistry()方法

[Spring->AbstractApplicationContext->refresh() ->invokeBeanDefinitionRegistryPostProcessors()->postProcessBeanDefinitionRegistry()]



postProcessBeanDefinitionRegistry 中利用 ConfigurationClassParser进行parse目标类（第一次默认是SpringBoot启动类：例如：BeanNam=conchSampleApplication，即你的业务启动类）



- parse 中会 调用 processConfigurationClass -> doProcessConfigurationClass 来处理配置类
  - doProcessConfigurationClass 中 调用 processImports 来处理 @Import 注解
  - 关键点：
  - @SpringBootApplication 上有一个 @EnableAutoConfiguration
  - 这个注解上有一个import:
  - @Import({**AutoConfigurationImportSelector**.class})
  - AutoConfigurationImportSelector 会被前置的getImports->collectImports给收集成SourceClass，并作为 processImports 的入参 importCandidates，processImports会将importCandidates进行实例化成ImportSelector，
  - 看下AutoConfigurationImportSelector的继承结构：
    implements DeferredImportSelector
  - 说明他是一个DeferredImportSelector，会交给 DeferredImportSelectorHandler进行处理，并最终被包装到DeferredImportSelectorHolder中



- DeferredImportSelectorHandler 在 ConfigurationClassParser parse的最后会被调用：

  - deferredImportSelectorHandler.process();

  - process() 方法 又委托给了DeferredImportSelectorGroupingHandler 进行处理 

  - 这里会先调用 **register** 进行group的注册，这一步有个关键点：

  - 源码是：

  - ```java
    // deferredImport = eferredImportSelectorHolder
    // getImportSelector() = DeferredImportSelector -> AutoConfigurationImportSelector
    // getImportGroup() = AutoConfigurationImportSelector.getImportGroup() 参考下面
    Class<? extends Group> group = deferredImport.getImportSelector().getImportGroup();
    
    // 重点：AutoConfigurationGroup
    public Class<? extends DeferredImportSelector.Group> getImportGroup() {
        return AutoConfigurationGroup.class;
    }
    
    // rigister 会实例化这个 group
    
    // 无用知识点：
    // AutoConfigurationGroup 是 private static class 切 其构造器是 private 的，那么外部的ConfigurationClassParser是如何实例化它的？
    // ParserStrategyUtils -> BeanUtils -> instantiateClass() -> instantiateClass() -> 
    // ReflectionUtils.makeAccessible(ctor); 对私有构造器进行了授权Constructor
    // ctor.setAccessible(true);
    ```

  - 最终是获得了一个 AutoConfigurationGroup

  - register 之后 调用 **processGroupImports()**

    - 使用上面注册的DeferredImportSelectorGrouping  调用 grouping.getImports()  

    - getImports() 会触发 AutoConfigurationGroup 的 process()  方法

    - process() 会获取 所有 AutoConfigurationEntry

    - 具体：process()  -> getAutoConfigurationEntry() -> getCandidateConfigurations() -> **SpringFactoriesLoader.loadFactoryNames()** 获取所有 META-INF/spring.factories 中，标注**org.springframework.boot.autoconfigure.EnableAutoConfiguration** 的配置信息

    - ```java
      // 无用知识点：Conditional 实现 ConfigurationClassFilter
      // AutoConfigurationImportFilter
      // - OnBeanCondition
      // - OnClassCondition
      // - OnWebApplicationCondition
      // - FilteringSpringBootCondition
      ```

    - getImports() 最终返回 ConfigurationClass ，会再循环解析 ConfigurationClass 上的 import 注解

    - processImports 之后，会处理 @Bean注解 retrieveBeanMethodMetadata













 









