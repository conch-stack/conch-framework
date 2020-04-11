## Spring源码分析（五）

> 依赖注入



- 依赖注入的模式和类型

  - 模式：

    - 手动模式 - 配置或编程的方式，提取安排注入规则
      - XML资源配置元信息
      - Java注解元信息
      - API配置元信息
    - 自动模式： - 实现方提供依赖自动关联的方式，按照内建的注入规则
      - Autowiring（自动绑定）

  - 类型：

    | 依赖注入类型 | 配置元数据举例                                   |
    | ------------ | ------------------------------------------------ |
    | Setter方法   | \<property name="user" ref="userBean" />         |
    | 构造器       | \<constructor-arg name="user" ref="userBean" />  |
    | 字段         | @Autowired User user;                            |
    | 方法         | @Autowired public void user(User user) { ... }   |
    | 接口回调     | class MyBean implements BeanFactoryAware { ... } |

    BeanFactoryAware会显式的传递一个BeanFactory给你用



- 自动绑定（Autowiring）模式
  - no: 默认值，Spring默认不推荐使用 AutowireCapableBeanFactory.AUTOWIRE_NO
  - byName：AutowireCapableBeanFactory.AUTOWIRE_BY_NAME
  - byType：AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE
  - constructor：AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR



- 自动绑定（Autowiring）限制与不足:
  - 容易产生错误绑定
  - 上下文存在多个同类型的bean时，容易产生歧义



- Setter方法依赖注入

  - 手动

    - XML：

      ```xml
      <bean name="userHolder" class="ltd.beihu.spring.dependency.injection.setter.UserHolder">
              <property name="user" ref="superUser" />
      </bean>
      ```

    - 注解：

      ```java
      @Bean
      public UserHolder userHolder(User user) {
          return new UserHolder(user);
      }
      ```

    - JAVA API：

      ```java
      private static BeanDefinition buildUserHolderApiBeanDefinition() {
          BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(UserHolderApi.class);
          // api 方式
          beanDefinitionBuilder.addPropertyReference("user", "user");
          return beanDefinitionBuilder.getBeanDefinition();
      }
      ```
  
  - 自动
  
    - byName：
  
      ```xml
      <bean name="userHolder" class="ltd.beihu.spring.dependency.injection.setter.UserHolder" autowire="byName">
      </bean>
      ```
  
    - byType：
  
      ```xml
      <bean name="userHolderBytype" class="ltd.beihu.spring.dependency.injection.setter.UserHolder" autowire="byType">
      </bean>
      ```



- 构造器依赖注入

  - 手动：

    - XML：

      ```xml
      <bean name="userHolder" class="ltd.beihu.spring.dependency.injection.setter.UserHolder">
          <constructor-arg name="user" ref="user" />
      </bean>
      ```

    - 注解：

      ```java
      @Bean
      public UserHolder userHolder(User user) {
          return new UserHolder(user);
      }
      ```

    - Java API：

      ```java
      private static BeanDefinition buildUserHolderApiBeanDefinition() {
          BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(UserHolderApi.class);
          // api 方式
          beanDefinitionBuilder.addConstructorArgReference("user");
          return beanDefinitionBuilder.getBeanDefinition();
      }
      ```

  - 自动：

    - constructor：

      ```xml
      <bean name="userHolder" class="ltd.beihu.spring.dependency.injection.setter.UserHolder" autowire="constructor">
      </bean>
      ```



- 字段依赖注入
  - 手动模式：
    - Java注解配置元信息
      - @Autowired：默认byType（会忽略静态字段的注入）
      - @Resource：默认byType
      - @Inject (可选)：JSR-330引入，需要单独引入依赖



- 方法依赖注入
  - 手动模式：
    - Java注解配置元信息
      - @Autowired：
      - @Resource：
      - @Inject：
      - @Bean：



- 接口回调依赖注入
  - Aware系列接口回调：
    - 自动模式：

| 内建接口                       | 说明                                                 |
| ------------------------------ | ---------------------------------------------------- |
| BeanFactoryAware               | 获取IOC容器 - BeanFactory对象                        |
| ApplicationContextAware        | 获取Spring应用上下文 - ApplicationContext对象        |
| EnvironmentAware               | 获取Environment对象                                  |
| ResourceLoaderAware            | 获取资源加载器 对象 - ResouceLoader                  |
| BeanClassLoaderAware           | 获取加载当前Bean Class的ClassLoader                  |
| BeanNameAware                  | 获取当前Bean的名称                                   |
| MessageSourceAware             | 获取MessageSource对象，用于Spring国际化              |
| ApplicationEventPublisherAware | 获取ApplicationEventPublishAware对象，用于Spring事件 |
| EmbeddedValueResolverAware     | 获取StringValueResolver对象，用于占位符处理          |



依赖注入类型选择



基础类型注入



集合类型注入



限定注入



延迟依赖注入



依赖处理过程



@Autowired注入原理



JSR-330 @Inject注入原理



Java通用注解注入原理



自定义依赖注入注解



### TODO

- BeanPostProcessor

- BeanFactoryPostProcessor