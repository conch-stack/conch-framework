# eihu-Framework

### Getting Started

### Spring IOC（Inversion of Control）
- BeanDefinition：Bean定义，Bean的元信息（单例、原型、初始化方法名，构造方式：构造器、工厂静态、工厂成员方法，销毁方法名、校验检查等）
  - GenericBeanDefinition
- BeanDefinitionRegistry：Bean定义的注册方法
- BeanFactory： Bean的抽象定义：类比所有水桶都要有基本的能盛水的功能
  - DefaultBeanFactory：实现 BeanFactory 和 BeanDefinitionRegistry 和 Closeable
    - 存放Bean定义Map<beanName,BeanDefinition>，存放Map<beanName,Object> 单例Bean
    - 1. 先准备一个Bean
      2. 将Bean的信息定义到GenericBeanDefinition中
      3. 将GenericBeanDefinition注册到DefaultBeanFactory的Bean定义Map中
      4. 使用DefaultBeanFactory的getBean方法创建/获取Bean：如果是单例的，放入Bean单例的Map中



### Spring DI（Dependency Injection）

#### `依赖注入的本质：赋值`

#### 依赖：

1. 构造参数依赖
2. 属性依赖



#### 参数、属性的值有哪些类型：

1. ##### 直接值

   1. 基本数据类型、String
   2. 数组、集合
   3. Map、Properties

2. ##### Bean依赖

##### 直接值：Bean工厂进行依赖注入时，就直接给入值



#### 如何定义参数依赖？



#### 如何定义构造参数依赖？

- List存储
- 参数顺序：按顺序放入List
- 参数值用：Object

​		List\<Object> constructorArgumentValues

- 参数是Object类型时，如何区分是直接值还是Bean依赖
  - 为Bean依赖定义一个数据类型（BeanReference）
  - Bean工厂在构造Bean实例时，遍历判断参数是否是BeanReference的，如果是，则替换为依赖的Bean实例
- BeanReference
  - 只需指明依赖的的Bean的名字






### Spring AOP