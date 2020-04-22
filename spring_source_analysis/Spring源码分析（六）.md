## Spring源码分析（六）

> 依赖来源



- 依赖查找来源
  - BeanDefinition
  - 单例对象
  - Spring内建BeanDefinition：参考AnnotationConfigUtils.java
  - Spring内建单例对象：参考AbstractApplicationContext#prepareBeanFactory

 

- 依赖注入来源
  - 



Spring容器管理和游离对象



Spring BeanDefinition 作为依赖来源



单例对象作为依赖来源



非Spring容器管理对象作为依赖来源



外部化配置作为依赖来源



