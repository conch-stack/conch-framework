## Spring源码分析（三）

Spring Bean基础

- 定义Bean
  - Spring中定义Bean的配置元信息接口
    - Bean的全限定名
    - Bean的行为配置元素，如作用域、自动绑定，生命周期回调等
    - 其他Bean引用，又称合作者或者依赖
    - 配置设置，比如Bean属性（Properties）



- **BeanDefinition元信息**

| 属性                     | 说明                                           |
| ------------------------ | ---------------------------------------------- |
| Class                    | Bean全类名，必须是具体的类，不能是抽象类或接口 |
| Name                     | Bean的名称或者ID                               |
| Scope                    | Bean的作用域（如：singleton、prototype等）     |
| Constructor Args         | Bean构造器参数（用于依赖注入）构造器注入       |
| Properties               | Bean的属性设置（用于依赖注入）属性注入         |
| Autowiring mode          | Bean的自动绑定模式（如：通过名称：byName）     |
| Lazy Initialization Mode | Bean的延迟初始化模式（延迟和非延迟）           |
| Initialization Method    | Bean初始化回调方法名称                         |
| Destruction Method       | Bean销毁回调的方法名称                         |

构建BeanDefinition：

- 通过BeanDefinitionBuilder
- 通过AbstractBeanDefinition及其派生类



- 命名 Spring Bean
- Spring Bean 别名
- 注册 Spring Bean
- 实例化 Spring Bean
- 延迟初始化 Spring Bean
- 销毁 Spring Bean
- 垃圾回收 Spring Bean