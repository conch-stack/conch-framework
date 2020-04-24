## Spring源码分析（六）

> 依赖来源



- 依赖查找来源

  - BeanDefinition

  - 单例对象

  - Spring内建BeanDefinition：

    - 参考AnnotationConfigUtils.java

  - Spring内建单例对象：

    - 参考 AbstractApplicationContext#prepareBeanFactory

      - ```java
        beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
        // environment
        // systemProperties
        // systemEnvironment
        ```

 

- 依赖注入来源

  ```java
  // 非Spring容器托管的Bean
  // 四个类，两个对象
  // 用Spring的BeanFactory无法获取这些非托管对象: 即beanFactory.getBean(BeanFactory.class)报错
  // 代码位置：AbstractApplicationContext#prepareBeanFactory
  beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
  beanFactory.registerResolvableDependency(ResourceLoader.class, this);
  beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
  beanFactory.registerResolvableDependency(ApplicationContext.class, this);
  // 我们可以通过 beanFactory.registerResolvableDependency 这个方法来注册自己的非托管Bean
  ```



- Spring容器管理和游离对象

  | 来源                  | SpringBean对象 | 生命周期管理 | 配置元信息 | 使用场景       |
  | --------------------- | -------------- | ------------ | ---------- | -------------- |
  | Spring BeanDefinition | 是             | 是           | 是         | 依赖查找、注入 |
  | 单体对象              | 是             | 否           | 无         | 依赖查找、注入 |
  | ResolvableDependency  | 否             | 否           | 无         | 依赖注入       |

  



- Spring BeanDefinition 作为依赖来源
  - BeanDefinitionRegistry -> DefaultListableBeanFactory 
    - -> #registerBeanDefinition(String beanName, BeanDefinition beanDefinition)



- 单例对象作为依赖来源
  - 要素：
    - 来源：外部普通Java对象
      - 不需要是POJO（JavaBeans中的定义：必须要有默认构造器，getter、setter等）
    - 注册：SingletonBeanRegistry#registerSingleton(String beanName, Object singletonObjec)
  - 限制：（未放入容器生命周期托管）
    - 无生命周期
    - 无法实现延迟初始化Bean
  - 依赖查找时，先查找单体对象，找到直接返回，没有在处理复杂的BeanDefiniton逻辑
    - AbstractBeanFactory#getBean



- 非Spring容器管理对象作为依赖来源

  - ConfigurableListableBeanFactory#registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue)
  - 限制：
    - 只能在依赖注入中使用
    - 只能通过类型来依赖注入
    - 无生命周期管理
    - 无法实现延迟初始化Bean
    - 无法通过依赖查找

  // TODO demo add



外部化配置作为依赖来源



