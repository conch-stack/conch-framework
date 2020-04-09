## Spring源码分析（五）

> 依赖注入



- 依赖注入的模式和类型

  - 模式：

    - 手动模式 - 配置或编程的方式，提取安排注入规则
      - XML资源配置元信息
      - Java注解元信息
      - API配置元信息
    - 自动模式： - 实现方提供依赖自动关联的方式，按照内建的注入规则
      - Autowiring（自动绑定）

  - 类型：

    | 依赖注入类型 | 配置元数据举例                                   |
    | ------------ | ------------------------------------------------ |
    | Setter方法   | \<property name="user" ref="userBean" />         |
    | 构造器       | \<constructor-arg name="user" ref="userBean" />  |
    | 字段         | @Autowired User user;                            |
    | 方法         | @Autowired public void user(User user) { ... }   |
    | 接口回调     | class MyBean implements BeanFactoryAware { ... } |

    BeanFactoryAware会显式的传递一个BeanFactory给你用



自动绑定（Autowiring）



自动绑定（Autowiring）模式



自动绑定（Autowiring）限制与不足



Setter方法依赖注入



构造器依赖注入



字段依赖注入



方法依赖注入



回调依赖注入



依赖注入类型选择



基础类型注入



集合类型注入



限定注入



延迟依赖注入



依赖处理过程



@Autowired注入原理



JSR-330 @Inject注入原理



Java通用注解注入原理



自定义依赖注入注解