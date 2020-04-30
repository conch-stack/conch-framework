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



- Spring Bean Class 加载阶段



- Spring Bean 实例化前阶段
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

![image-20200430040203289](assets/image-20200430040203289.png)