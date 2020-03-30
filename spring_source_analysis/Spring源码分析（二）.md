## Spring源码分析（二）

IoC



##### Spring IoC

- 依赖查找
  - 根据 Bean 名称查找：
    - 实时查找：直接用 beanFactory.getBean("user")
    - 延迟查找: 用ObjectFactory\<User>将User包装一下，进行实例化
  - 根据 Bean 类型查找：
    - 单个 Bean 对象：beanFactory.getBean(User.class)
    - 集合 Bean对象：listableBeanFactory.getBeansOfType(User.class)
  - 根据 Bean 名字 + 类型查找
  - 根据 Java 注解查找：listableBeanFactory.getBeansWithAnnotation(Super.class)
    - 单个 Bean 对象
    - 集合 Bean 对象

- 依赖注入
  - 根据Bean名称注入： \<ref bean="superUser"/>
  - 根据Bean类型注入：autowire="byType"
    - 单个Bean对象
    - 集合Bean对象
  - 注入容器内建Bean对象：
  - 注入非Bean对象
  - 注入类型
    - 实时注入
    
    - 延迟注入
    
      - ```java
        private ObjectFactory<User> objectFactory; // 延迟注入的属性
        ```
  
- 依赖来源

  - 自定义Bean

  - 容器内建Bean对象

    - ```java
      // 容器内建 Bean
      Environment environment = beanFactory.getBean(Environment.class);
      System.out.println("获取Environment类型的Bean：" + environment);
      ```

  - 容器内建依赖

    - 不可以通过 beanFactory.getBean("xxx") 去获取



##### ApplicationContext 和 BeanFactory的关系

ApplicationContext  实现了 BeanFactory接口，同时又组合了一个BeanFactory，所以在用ApplicationContext时，一定要去获取去真正的BeanFactory

类似代理；

官方介绍：

In short, the `BeanFactory` provides the configuration framework and basic functionality, and the `ApplicationContext` adds more enterprise-specific functionality.

简而言之，BeanFactory提供了配置框架和基本功能，而ApplicationContext增加了更多企业特定的功能。

ApplicationContext提供：

- Easier integration with Spring’s AOP features：更容易与Spring AOP集成
- Message resource handling (for use in internationalization)：消息处理，用于国际化
- Event publication：事件发布
- Application-layer specific contexts such as the `WebApplicationContext` for use in web applications.：应用级别的上下文，如给web应用使用的WebApplicationContext



##### Spring IoC配置元信息

- Bean定义配置
  - 基于XML文件
  - 基于Properties文件
  - 基于Java注解
  - 基于Java API
- IoC容器配置
  - 基于XML文件
  - 基于Java注解
  - 基于Java API
- 外部化属性配置
  - 基于Java注解：@Value("")