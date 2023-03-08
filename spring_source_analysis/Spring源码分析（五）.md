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



- 依赖注入类型选择
  - 低依赖：构造器注入
  - 多依赖：Setter方法注入
  - 便利性：字段注入
  - 声明类：方法注入



- 基础类型注入
  - 基础类型：
    - 原生类型（Primitive）：boolean、byte、char、short、int、float、long、double
    - 标量类型（Scalar）：Number、Character、Boolean、Enum、Locale、Charset、Currency、Properties、UUID
    - 常规类型（General）：Object、String、TimeZone、Calendar、Optional等
    - Spring类型：Resource、InputSource、Formatter等



- 集合类型注入
  - 集合类型：
    - 数组类型（Array）：原生类型、标量类型、常规类型、Spring类型
    - 集合类型（Collection）：
      - Collection：List、Set（SortedSet、NavigableSet、EnumSet）
      - Map：Properties



- 限定注入

  - 使用注解@Qualifier限定

    - 通过Bean名称限定

    - 通过分组限定

      - ```
        加了@Qualifier之后，就被分组了，全部的就看不了他们的，所以别的用的地方必须同样加上 @Qualifier
        ```

  - 基于注解@Qualifier扩展限定

    - 自定义注解 - 如 Spring Cloud 中的 @LoadBalance：实现了RestTemplate分组，一种带负载均衡一种不带



- 延迟依赖注入
  - 使用API ObjectFactory延迟注入
    - 单一类型
    - 集合类型
  - 使用API ObjectProvider延迟注入（推荐）
    - 单一类型
    - 集合类型
  - 在依赖处理过程中，会被DefaultListableBeanFactory处理为一个CGLib代理对象，并不会进行实例化，当用到时才会实例化



- 依赖处理过程
  - 入口：DefaultListableBeanFactory#resolveDependency
  - 依赖描述符：DependencyDescriptor
  - 自动绑定候选对象处理器：AutowireCandidateResolver



- @Autowired注入原理
  - 元信息解析
  - 依赖查找
  - 依赖注入（字段、方法）
  - **源码分析：**
  - AutowiredAnnotationBeanPostProcessor#postProcessMergedBeanDefinition 循环合并父子属性和方法，**构建 InjectionMetadata**，并进行校验 + 缓存
  - AutowiredAnnotationBeanPostProcessor#postProcessProperties 获取InjectionMetadata，**执行其 inject注入方法**， InjectionMetadata内部存储了当前主Bean（需要被注入的类）的所有需要注入的属性或方法（构造器）的 InjectedElement 对象，最终会执行其inject进行依赖处理查找注入等工作
  - **解析 InjectionMetadata 内部逻辑：**
    - 构建：InjectionMetadata
      - 构建：InjectedElement
        - AutowiredFieldElement：字段自动注入元信息封装
        - AutowiredMethodElement：方法自动注入元信息封装
          - PropertyDescriptor -> GenericTypeAwarePropertyDescriptor
      - \#inject(Object bean, String beanName, PropertyValues pvs) 注入
        - 构建 DependencyDescriptor （依赖描述）
          - DependencyDescriptor[]  方法Autowired构建依赖描述数组
        - 利用 beanFactory.resolveDependency 处理依赖
        - 注册 beanFactory.registerDependentBean 依赖情况
        - 赋值：
          - field.set(bean, value)   字段注入
          - method.invoke(bean, arguments)  方法注入



- JSR-330 @Inject注入原理

  - 注入过程基本类似@Autowired，复用了AutowiredAnnotationBeanPostProcessor
  - 只是需要额外引入依赖包，且其优先级最低

  ```java
  public AutowiredAnnotationBeanPostProcessor() {
  		this.autowiredAnnotationTypes.add(Autowired.class);
  		this.autowiredAnnotationTypes.add(Value.class);
  		try {
  			this.autowiredAnnotationTypes.add((Class<? extends Annotation>)
  					ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
  			logger.trace("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
  		}
  		catch (ClassNotFoundException ex) {
  			// JSR-330 API not available - simply skip.
  		}
  	}
  ```



- Java通用注解注入原理
  - ConfigurationClassPostProcessor
    - 实现自：BeanFactoryPostProcessor
    - 用于解析 @Configuration  @Bean注解
  - CommonAnnotationBeanPostProcessor
    - 注入注解：
      - javax.xml.ws.WebServiceRef
      - javax.ejb.EJB
      - javax.annotation.Resource
    - 生命周期注解：
      - javax.annotation.PostConstant
      - javax.annotation.PreDestroy
  - XXXBeanPostProcessor是有顺序的
    - @see org.springframework.core.PriorityOrdered 定义了Order顺序，默认优先级最低
    - @Order 默认是最低优先级,值越小优先级越高
    - CommonAnnotationBeanPostProcessor为 Ordered.LOWEST_PRECEDENCE - 3
    - AutowiredAnnotationBeanPostProcessor为 Ordered.LOWEST_PRECEDENCE - 2
    - 所以 CommonAnnotationBeanPostProcessor 比 AutowiredAnnotationBeanPostProcessor先执行
    - 注意：@Order只能定义类的执行顺序，不能决定其创建顺序
      - **他们创建顺序，定义在AnnotationConfigUtils#registerAnnotationConfigProcessors方法中，构建Spring内建BeanDefinition，放入LinkedHashSet中**
      - **AnnotationConfigUtils#registerAnnotationConfigProcessors方法由xml触发，或者由 AnnotationConfigApplicationContext中的AnnotatedBeanDefinitionReader初始化时触发**



- 自定义依赖注入注解

  - 复用 AutowiredAnnotationBeanPostProcessor

  ```java
  @Bean
  @Order(Ordered.LOWEST_PRECEDENCE - 3)
  public static AutowiredAnnotationBeanPostProcessor injectUserProcessor() {
    AutowiredAnnotationBeanPostProcessor p = new AutowiredAnnotationBeanPostProcessor();
    p.setAutowiredAnnotationType(InjectUser.class);
    return p;
  }
  ```

  - 完全自定义开发：特殊需求



### 

- BeanPostProcessor：针对某个Bean进行实例化前后的操作

- BeanFactoryPostProcessor：针对整个BeanFactroy进行容器的一些操作