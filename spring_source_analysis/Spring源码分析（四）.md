## Spring源码分析（四）

> 依赖查找



- 单一类型依赖查找：BeanFactory

  ```
  1. 按照Bean的名称查找（略）
  2. 按照Bean的类型查找
           实时:
           		getBean(String name)
           延迟：
               ObjectFactory
               ObjectProvider （Spring 5.1 引入）
               		applicationContext.getBeanProvider(Class<T> requiredType);
               		getBeanProvider(ResolvableType requiredType); // 泛型 
  3. 按照Bean名称+类型查找
  ```

- 集合类型依赖查找：ListableBeanFactory

  - 根据Bean类型查找
    - 获取同类型Bean名称的列表：(可先不实例化)
      - getBeanNamesForType(Class<?> type) 
      - getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit)
      - getBeanNamesForType(ResolvableType type)
    - 获取同类型Bean实例列表：
      - getBeansOfType(Class) 以及其重载方法
  - 根据注解类型查找
    - Spring3.0 获取标注类型Bean名称列表
      - getBeanNamesForAnnotation(Class<? extends Annotation>)
    - Spring3.0 获取标注类型Bean实例列表
      - getBeansWithAnnotation(Class<? extends Annotation> )
    - Spring3.0 获取指定名称 + 注解类型 的Bean实例
      - findAnnotationOnBean(String, Class<? extends Annotation>)



- 层次依赖查找：HierarchicalBeanFactory
  - 双亲BeanFactory：getParentBeanFactory()
  - 层次性查找：
    - 根据Bean的名称查找
      - 基于 containsLocalBean() 方法实现
    - 根据Bean类型查询实例列表
      - 单一类型：BeanFactoryUtils#beanOfType
      - 集合类型：BeanFactoryUtils#beansOfTypeIncludingAncestors
    - 根据 Java注解查找名称列表
      - BeanFactoryUtils#beanNamesForTypezIncludingAncestors



- 延迟依赖查找
  - ObjectFactory
  - ObjectProvider
    - getObject()
    - getIfAvailable()
    - getIfUnique()
    - iterator()
    - stream()



- 安全依赖查找 (NoSuchBeanException...)

| 依赖查找类型 | 代表实现                           | 是否安全 |
| ------------ | ---------------------------------- | -------- |
| 单一类型查找 | BeanFactory#getBean                | 否       |
|              | ObjectFactory#getObject            | 否       |
|              | ObjectProvider#getIfAvailable      | 是       |
|              |                                    |          |
| 集合类型查找 | ListableBeanFactory#getBeansOfType | 是       |
|              | ObjectProvider#stream              | 是       |

>  层次依赖查找的安全性取决于其扩展的单一或集合类型的BeanFactory接口

尽量使用：ObjectProvider 来查找Bean



- 内建可查找的依赖
  - AbstractApplicationContext内建可查找依赖
  
  | Bean名称                    | Bean实例                    | 使用场景               |
  | --------------------------- | --------------------------- | ---------------------- |
  | environment                 | Environment对象             | 外部化配置以及Profiles |
  | systemProperties            | java.util.Properties对象    | Java系统属性           |
  | systemEnvironment           | java.util.Map对象           | 操作系统环境变量       |
  | messageSource               | MessageSource对象           | 国际化文案             |
  | lifecycleProcessor          | LifecycleProcessor对象      | Lifecycle Bean处理器   |
  | applicationEventMulticaster | ApplicationEventMulticaster | Spring事件广播器       |
  
  - 注解驱动Spring应用上下文内建可查找的依赖（部分）| bean名称较长 （AnnotationConfigUtils）

| Bean名称                                                     | Bean实例                                         | 使用场景                                            |
| ------------------------------------------------------------ | ------------------------------------------------ | --------------------------------------------------- |
| org.springframework.context.annotation.<br />internalConfigurationAnnotationProcessor | ConfigurationClass<br />PostProcessor对象        | 处理Spring配置类<br />@Configuration                |
| org.springframework.context.annotation.<br />internalAutowiredAnnotationProcessor | AutowiredAnnotation<br />BeanPostprocessor对象   | 处理@Autowired以及@Value注解                        |
| org.springframework.context.annotation.<br />internalCommonAnnotationProcessor | CommonAnnotation<br />BeanPostProcessor对象      | （条件激活）处理JSR-250注解，如：@PostConstruct     |
| org.springframework.context.event.<br />internalEventListenerProcessor | EventListenerMethodProcessor对象                 | 处理标注@EventListener的Spring事件监听方法          |
| org.springframework.context.event.<br />internalEventListenerFactory | DefaultEventListenerFactory对象                  | @EventListener事件监听方法适配为ApplicationListener |
| org.springframework.context.annotation.<br />internalPersistenceAnnotationProcessor | PersistenceAnnotation<br />BeanPostPrecessor对象 | (条件激活)处理JPA注解场景                           |



- 依赖查找中的经典异常
  - NoSuchBeanDefinitionException：当Bean不存在与IOC容器时
  - NoUniqueBeanDefinitionException：类型依赖查找时，IOS容器存在多个Bean实例
  - BeanInstantiationException：当Bean所对应的类型非具体类时（抽象类、接口等）
  - BeanCreationException：当Bean初始化过程中发生异常
  - BeanDefinitionStoreException：当BeanDefinition配置元信息非法时

