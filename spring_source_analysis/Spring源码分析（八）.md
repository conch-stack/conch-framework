## Spring源码分析（八）

> Spring Bean 生命周期



- Spring Bean元信息配置阶段

  - XML: XmlBeanDefinitionReader
  - Properties：PropertiesBeanDefinitionReader
  - Groovy

  

- Spring Bean元信息解析阶段
  - 面向资源的BeanDefinition解析
    - BeanDefinitionReader
      - 指定资源进行读取解析
      - XML解析器 - BeanDefinitionParser
  - 面向注解的BeanDefinition解析
    - AnnotationBeanDefinitionReader
      - 指定包或者class文件进行解析
  - API方式



- Spring Bean注册阶段

  - BeanDefinitionRegistry

    - DefaultListableBeanFactroy

    ```java
    /** 
     * Map of bean definition objects, keyed by bean name.  
     * BeanName->BeanDefinition映射
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    
    /** List of bean definition names, in registration order.  保证顺序 */
    private volatile List<String> beanDefinitionNames = new ArrayList<>(256);
    
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
    ```

    



- Spring BeanDefinition合并阶段
  - 父子BeanDefinition合并
    - 当前BeanFactory查找
    - 层次BeanFactory查找
  - RootBeanDefinition不需要合并
    - 不允许设置parent
  - GenericBeanDefinition：默认一开始所有构建的BeanDefinition都是GenericBeanDefinition，在合并时会逐步递归，最终变成RootBeanDefinition；注意这个Merge的过程是不会改变原有BeanDefinition的，只会copy一份过来，重写当前被merge的BeanDefinition 



- Spring Bean Class 加载阶段

  - ClassLoader类加载：利用Java基础的ClassLoader进行加载

  - Java Security 安全控制

  - ConfigurableBeanFactory 临时 ClassLoader

  - BeanDefinition

    - getBeanClassName()
    - getParentName()
    - getFactoryBeanName()
    - getFactoryMethodName()

  - getBean()

    - 先去单例对象注册中心查找：（这类Bean是无Spring的生命周期的）找到直接返回

    - 没有找到会调用singletonFactory.getObject(): 会调用createBean(beanName, mbd, args)

    - 在子类：AbstractAutowireCapableBeanFactory中实现了createBean

    - resolveBeanClass(mbd, beanName)：获取Class对象

    - hasBeanClass(): 判断当前的BeanDefinition是否已经存在Class对象了

    - ```java
      @Nullable
      private volatile Object beanClass;  // 在AbstractBeanDefinition中定义为Object
      ```

    - 一开始这个值是一个String类型：所有为 false

    - ```java
      /**
       * Return whether this definition specifies a bean class.
       * @see #getBeanClass()
       * @see #setBeanClass(Class)
       * @see #resolveBeanClass(ClassLoader)
       */
      public boolean hasBeanClass() {
         return (this.beanClass instanceof Class);
      }
      ```

    - false后，触发Class构建 ：doResolveBeanClass(mbd, typeMatch)：

      - typeMatch为空无所谓，有值的话是指定类型去构建Class

    - doResolveBeanClass() 中会获取 ClassLoader 和 dynamicClassLoader（临时ClassLoader）

      - 边角：BeanClassLoaderAware 可获取ClassLoader

    - mdb.resolveBeanClass(beanClassLoader)： AbstractBeanDefinition中处理Class的加载

    - 将之前的String类型的 beanClass 直接替换为 Class对象



- Spring Bean 实例化前阶段

  - InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation()
  - 会打破Spring的注册
  - 调用链路：getBean#createBean#resolveBeforeInstantiation

  

- Spring Bean 实例化阶段



- Spring Bean 实例化后阶段



- Spring Bean 属性赋值前阶段



- Spring Bean Aware接口回调阶段



- Spring Bean 初始化前阶段
- Spring Bean 初始化阶段
- Spring Bean 初始化后阶段
- Spring Bean 初始化完成阶段



- Spring Bean 销毁前阶段
- Spring Bean 销毁阶段
- Spring Bean 垃圾收集



FactoryBean的处理逻辑：

- RootBeanDefinition

```java
/** Package-visible field for caching if the bean is a factory bean. */
@Nullable
volatile Boolean isFactoryBean;
```



![](assets/image-20200430040203289.png)



