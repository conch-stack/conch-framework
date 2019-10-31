# Beihu-Framework



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



TODO：参考https://github.com/seaswalker

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

- List存储
- BeanDefinition中定义获取属性List接口
- GenericBeanDefinition中实现这个方法，定义List存储结构
- DefaultBeanFactory在构造完Bean后，获取从BeanDefinition中获取参数依赖、循环赋值
- 赋值同样涉及直接值和Bean依赖问题，同构造参数依赖一样



#### 如何定义构造参数依赖？

- List存储 
- BeanDefinition中定义获取属性List接口
- GenericBeanDefinition中实现这个方法，定义List存储结构
- 参数顺序：按顺序放入List
- 参数值用：Object

​		List\<Object> constructorArgumentValues

- 参数是Object类型时，如何区分是直接值还是Bean依赖
  - 为Bean依赖定义一个数据类型（BeanReference）
  - Bean工厂在构造Bean实例时，遍历判断参数是否是BeanReference的，如果是，则替换为依赖的Bean实例
- BeanReference
  - 只需指明依赖的的Bean的名字
- 处理循环依赖
  - 添加正在构建Set数组，check是否正在构建，构建完后，删除之。



#### Bean增加别名支持（别名也是唯一的）





### Spring AOP（Aspect Oriented Programming）

#### 分析

- 进行功能增强：功能
  - Advice通知：要进行功能增强的逻辑
- 对类方法增强：可选择要增强的方法
  - Pointcuts切入点：寻找功能增强的点
- 不改变原类去情况下，实现增强
  - Weaving织入：代理



#### Advice设计

- 特点：可选时机：方法功能前、后、异常

定义一套标准接口，由用户实现

接口类图

- Advice
  - MethodBeforeAdvice
    - before(Method method, Object[] args, Object object)
  - AfterReturningAdvice
    - afterReturning(Object returnValue, Method method, Object[] args, Object object)
  - MethodInterceptor 环绕
    - invoke(Method method, Object[] args, Object object):Object



#### Pointcut设计

分析；

- 指定哪些方法需要切入

- xx类的xx方法

- 重载问题：需要参数类型

完整的需要的是一个：方法签名

- com.beihu.zjz.Test.testMethod(Boy, Time)
- com.beihu.zjz.Test.testMethod(Boy, Girl, Time)

需要一个表达式：能够描述多个方法，模糊匹配

- 包名：有父子特点，要能模拟匹配
- 类名：要能模拟匹配
- 方法名：要能模拟匹配
- 参数类型：参数可以多个

匹配方式：

- 正则表达式  - yes
- Ant Path表达式
- AspectJ的pointcut表达式  - yes
  - execution(* com.beihu.zjz.service.AccountService.*(..))

匹配类、匹配方法

接口类图

- Pointcut
  - matchClass(Class<?> targetClass):boolean
  - matchMethod(Method method, Class<?> targetClass):boolean



#### Aspect设计

组合Advice和Pointcut

类图

- Advisor
  - getAdviceBeanName:String
  - getExpression():Stirng





#### Weaving设计

织入时间：创建Bean实例的时候，在**Bean初始化**后，进行增强

- 初始化bean
- 判断bean是否要增强
  - 如果要增强：代理增强，返回实例
  - 如果不要增强，直接返回实例

**扩展：**（观察者模式）

> 1. 创建Bean定义
> 2. 注册Bean定义
> 3. 创建Bean实例
> 4. 初始化Bean实例
> 5. ...

在每个步骤前后，都可添加事件，触发观察者更新



定义观察者接口

- BeanPostProcessor
  - postProcessBeforeInitialization(Object bean, String beanName):Object
  - postProcessAfterInitialization(Object bean, String beanName):Object
- AdvisorAutoProxyCreator implement BeanPostProcessor, AdvisorRegistry : 实现观察者
  - postProcessAfterInitialization(Object bean, String beanName):Object
  - List\<Advisor> 注册所有的切面到代理里面来
- BeanFactory
  - registerBeanPostProcess(BeanPostProcessor bpp)  : 注册到Bean工厂





### Mybatis设计

1. 设计一个类存放从xml和注解获得的SQL映射信息

```
public class MappedStatement {

    /**
     * 唯一编号：完整类名+方法名
     */
    private String id;

    /**
     * sql
     */
    private String sql;

    /**
     * sql命令类型
     */
    private SqlCommandType sqlCommandType;

}

```

2. 存放MappedStatement

```
public class Configuration {

    /**
     * key = MappedStatement.id
     */
    private Map<String, MappedStatement> mappedStatements;

    public void addMappedStatement(MappedStatement mappedStatement) {
        mappedStatements.put(mappedStatement.getId(), mappedStatement);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public boolean hasMappedStatement(String id) {
        return mappedStatements.containsKey(id);
    }

}
```