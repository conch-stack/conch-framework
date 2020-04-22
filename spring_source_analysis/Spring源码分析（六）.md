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



Spring容器管理和游离对象



Spring BeanDefinition 作为依赖来源



单例对象作为依赖来源



非Spring容器管理对象作为依赖来源



外部化配置作为依赖来源



