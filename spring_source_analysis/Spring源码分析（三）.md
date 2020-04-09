## Spring源码分析（三）

> Spring Bean基础

- **定义Bean**
  - Spring中定义Bean的配置元信息接口
    - Bean的全限定名
    - Bean的行为配置元素，如作用域、自动绑定，生命周期回调等
    - 其他Bean引用，又称合作者或者依赖
    - 配置设置，比如Bean属性（Properties）



- **BeanDefinition元信息**
  - 构建BeanDefinition：
    - 通过BeanDefinitionBuilder
    - 通过AbstractBeanDefinition及其派生类 GenericBeanDefinition
      - **AnnotatedBeanDefiniton** 标注注解的Bean定义

| 属性                     | 说明                                           |
| ------------------------ | ---------------------------------------------- |
| Class                    | Bean全类名，必须是具体的类，不能是抽象类或接口 |
| Name                     | Bean的名称或者ID                               |
| Scope                    | Bean的作用域（如：singleton、prototype等）     |
| Constructor Args         | Bean构造器参数（用于依赖注入）构造器注入       |
| Properties               | Bean的属性设置（用于依赖注入）属性注入         |
| Autowiring mode          | Bean的自动绑定模式（如：通过名称：byName）     |
| Lazy Initialization Mode | Bean的延迟初始化模式（延迟和非延迟）           |
| Initialization Method    | Bean初始化回调方法名称                         |
| Destruction Method       | Bean销毁回调的方法名称                         |



- **命名 Spring Bean**
  - Bean的id或name，别名
  - Spring不强求要求一点要指定Bean的id或name
    - 未指定，会自动生成（Java驼峰规则）
      - BeanNameGenerator，DefaultBeanNameGenerator
      - AnnotationBeanNameGenerator



- **Spring Bean 别名**

  - 复用现有的BeanDefinition

  

- **注册 Spring Bean**
  - XML配置元信息
    - <bean name="" ... />
  - Java注解配置元信息
    - @Bean
    - @Component
    - @Import
  - Java API配置元信息
    - 命名方式：BeanDefinitionRegistry#registerBeanDefinition(String, BeanDefinition)
    - 非命名方式：BeanDefinitionReaderUtils#registerWithGeneratedName(AbstractBeanDefinition,BeanDefinitionRegistry)
    - 配置类方式：AnnotatedBeanDefinitionReader#register(Class...)
  - 注册外部单例Bean
    - 可将外部Bean对象托管给Spring进行管理
      - BeanFactory.registerSingleton(String 名称, BeanInstance 实例)



- 实例化 Spring Bean
  - 常规方法：
    - 通过构造器（配置元信息：XML、注解、JAVA API）
    - 通过静态工厂方法（配置元信息：XML、JAVA API）
    - 通过Bean工厂（配置元信息：XML、JAVA API）
    - 通过FactoryBean（配置元信息：XML、注解、JAVA API）
  - 特殊方法：
    - 通过 ServiceLoaderFactoryBean（配置元信息：XML、注解、JAVA API）
    - 通过 AutowireCapableBeanFactory#createBean(Class, int, boolean)
    - 通过BeanDefinitionRegistry#registerBeanDefinition(String, BeanDefinition)



- 初始化 Spring Bean
  - @PostConstruct标注的方法
  - 实现InitializingBean接口的 afterPropertiesSet() 方法
  - 自定义初始化方法
    - XML: <bean init-method="initXXX" ../>
    - Java注解：@Bean(initMethod="initXXX")
    - JavaAPI方法：AbstractBeanDefinition#setInitMethodName(String)
  
  
  
  > 如果三个定义在一个Bean中，执行顺序为：
  >
  > @PostConstuct -> InitializingBean -> 自定义初始化方法



- 延迟初始化 Spring Bean

  - XML配置：<bean lazy-init="true" .../>
  - Java注解：@Lazy(true)

  延迟与非延迟的区别：

  ```java
  // 启动应用上下文 TODO 会默认启动非延迟初始化的的Bean
  applicationContext.refresh();
  // 区别在于，初始化的时间，默认Spring应用上下文在启动后自动初始化非延迟加载的Bean
  // 延迟加载的Bean会在依赖查找或者依赖注入的时候进行初始化
  ```



- 销毁 Spring Bean

  - @PreDestory标注方法（Java原生的，在被垃圾回收之前进行操作，在Spring场景下就是销毁Bean）

  - 实现 DisposableBean 接口的 destory() 方法

  - 自定义销毁方法：

    - XML 配置： <bean destory="destory" .../>
    - Java 注解：@Bean(destory = "destory")
    - Java API方法：AbstractBeanDefinition#setDestoryMethodName(String)

    

  > 如果三种方法定义在同一个Bean中，执行顺序为：
  >
  > @PreDestory -> DisposableBean -> 自定义销毁方法



- 垃圾回收 Spring Bean
  - 触发顺序
    - 关闭Spring 容器 （应用上下文）
    - 执行GC
    - Spring Bean 覆盖的 finalize() 方法被回调

