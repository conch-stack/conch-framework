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



ApplicationContext除了IOC容器角色，还提供：

- 面向切面（AOP）
- 配置元信息（Configuration Metadata）
- 资源管理（Resources）
- 事件（Events）
- 国际化（i18n）
- 注解（Annotations）
- Environment 抽象（Environment Abstraction）



##### 什么时候用ApplicationContext and BeanFactory

```java
/**
 * BeanFactory 作为 IOC 容器
 *
 * TODO 用这个就没有那些事件等复杂的支持了
 *
 * @author Adam
 * @since 2020/3/30
 */
public class BeanFactoryAsIoCContainerDemo {

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 加载配置
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        int beansNum = reader.loadBeanDefinitions("classpath:META-INF/dependency-lookup-context.xml");
        System.out.println("加载的Bean的个数：" + beansNum);

        // 依赖查找
        lookupCollectionType(beanFactory);
    }


    private static void lookupCollectionType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("查找所有类型为User的Bean：" + users);
        }
    }
}
```



```java
/**
 * Application 作为 IOC 容器
 *      注解能力的 context
 *
 * @author Adam
 * @since 2020/3/30
 */
@Configuration
public class ApplicationContextAsIoCContainerDemo {

    public static void main(String[] args) {
        // 创建 ApplicationContext 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册当前类 作为 配置类 Configuration Class
        applicationContext.register(ApplicationContextAsIoCContainerDemo.class);
        // 启动应用上下文
        applicationContext.refresh();
        // 依赖查找
        lookupCollectionType(applicationContext);
        // 停止
        applicationContext.close();
    }

    @Bean
    public User user() {
        return new User("ApplicationContext", 10);
    }

    private static void lookupCollectionType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("查找所有类型为User的Bean：" + users);
        }
    }
}
```



##### IoC生命周期

- 启动:applicationContext.refresh();
- 运行
- 停止:applicationContext.close();



##### BeanFactory 和 FactoryBean 的区别

- BeanFactory是IoC的底层容器

- FactoryBean是创建Bean的一种方式，帮助实现复杂的初始化逻辑

  ```java
  public interface FactoryBean<T> {
  
     String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";
  
     /**
      * 获取对象
      */
     @Nullable
     T getObject() throws Exception;
  
     /**
      * 获取对象类型
      */
     @Nullable
     Class<?> getObjectType();
  
     /**
      * 是否为单例
      */
     default boolean isSingleton() {
        return true;
     }
  }
  ```

  FactoryBean会被BeanFactory容器调用，根据其类型和是否为单例进行对象创建

  问题：FactoryBean被创建的Bean会不会被纳入Bean的生命周期



##### Spring IoC容器启动做了哪些准备？

- IoC配置元信息的读取和解析（XML、Annotation）
- IoC容器生命周期 （refresh、postProcessor）
- Spring事件发布
- 国际化
- ....