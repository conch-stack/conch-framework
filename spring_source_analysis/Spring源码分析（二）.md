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

ApplicationContext  实现了 BeanFactory接口，同时又组合了一个BeanFactory，所有在用ApplicationContext时，一定要去获取去真正的BeanFactory