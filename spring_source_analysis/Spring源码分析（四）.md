## Spring源码分析（四）

依赖查找



- 单一类型依赖查找：BeanFactory

  ```
  1. 按照Bean的名称查找（略）
  2. 按照Bean的类型查找
           实时:
           		getBean(String name)
           延迟：
               ObjectFactory
               ObjectProvider （Spring 5.1 引入）
               		applicationContext.getBeanProvider(Class<T> requiredType);
               		getBeanProvider(ResolvableType requiredType); // 泛型 
  3. 按照Bean名称+类型查找
  ```

- 集合类型依赖查找：ListableBeanFactory

  - 根据Bean类型查找
    - 获取同类型Bean名称的列表：(可先不实例化)
      - getBeanNamesForType(Class<?> type) 
      - getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit)
      - getBeanNamesForType(ResolvableType type)
    - 获取同类型Bean实例列表：
      - getBeansOfType(Class) 以及其重载方法
  - 根据注解类型查找
    - Spring3.0 获取标注类型Bean名称列表
      - getBeanNamesForAnnotation(Class<? extends Annotation>)
    - Spring3.0 获取标注类型Bean实例列表
      - getBeansWithAnnotation(Class<? extends Annotation> )
    - Spring3.0 获取指定名称 + 注解类型 的Bean实例
      - findAnnotationOnBean(String, Class<? extends Annotation>)



- 层次依赖查找：HierarchicalBeanFactory
  - 双亲BeanFactory：getParentBeanFactory()
  - 层次性查找：
    - 根据Bean的名称查找
      - 基于 containsLocalBean() 方法实现
    - 根据Bean类型查询实例列表
      - 单一类型：BeanFactoryUtils#beanOfType
      - 集合类型：BeanFactoryUtils#beansOfTypeIncludingAncestors
    - 根据 Java注解查找名称列表
      - BeanFactoryUtils#beanNamesForTypezIncludingAncestors



- 延迟依赖查找
  - ObjectFactory
  - ObjectProvider
    - getObject()
    - getIfAvailable()
    - getIfUnique()
    - iterator()
    - stream()



- 安全依赖查找 (NoSuchBeanException...)

| 依赖查找类型 | 代表实现                           | 是否安全 |
| ------------ | ---------------------------------- | -------- |
| 单一类型查找 | BeanFactory#getBean                | 否       |
|              | ObjectFactory#getObject            | 否       |
|              | ObjectProvider#getIfAvailable      | 是       |
|              |                                    |          |
| 集合类型查找 | ListableBeanFactory#getBeansOfType | 是       |
|              | ObjectProvider#stream              | 是       |

>  层次依赖查找的安全性取决于其扩展的单一或集合类型的BeanFactory接口

尽量使用：ObjectProvider 来查找Bean



内建可查找的依赖

依赖查找中的经典异常

