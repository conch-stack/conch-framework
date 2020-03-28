## Spring源码分析（一）

Spring核心特性：![Spring核心特性](assets/Spring核心特性.png)

### Spring编程模型

-  面向对象编程

  - 契约接口：Aware、BeanPostProcessor....
  - 设计模式：观察者模式、组合模式、模板模式...
  - 对象基础：Abstract* 类

- 面向切面编程

  - 动态代理：JdkDynamicAopProxy
  - 字节码提升：ASM、CGLib、AspentJ...

- 面向元编程

  - 注解：模式驱动 (@Component、 @Service、@Respository...)
  - 配置：Environment抽象、PropertySource、BeanDefinition...
  - 泛型：GenericTypeResolver、ResolvableType....

- 函数驱动

  - 函数接口：ApplicationEventPublisher

    ​					Reactive：Spring WebFlux

- 模块驱动

  - Maven Artifacts
  - Java9 Automatic Modules
  - Spring @Enable*



### 设计思想

- OOP：Object-Oriented Programming
- IOC/DI
- DDD：Domain-Driven Development
- TDD：Test-Driven Development
- EDP：Event-Driven Programming
- FP：Functional Programming



### 设计模式

- 专属设计模式：
  - 前缀模式
    - Enable模式
    - Configurable模式
  - 后缀模式
    - 处理器模式
      - Processor
      - Resolver
      - Handler
    - 意识模式
      - Aware
    - 配置器模式
      - Configurer
    - 选择器模式
      - org.springframework.context.annotation.ImportSelector
- GoF 23







### 其他

@Indexed注解？？？

DateTimeContext

StreamConverter

CompletableToListenableFutureAdapter