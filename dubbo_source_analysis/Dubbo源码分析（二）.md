## Dubbo源码分析（二） - 注册中心实现



- 对象结构
- 对象结构如何初始化
- 如何进行协作



##### 调用链路：

ReferenceConfig（引用对象） -> UserService（服务接口、Proxy） -> Invoker（执行器:FailoverClusterInvoker）-> Directory -> URLs（提供者列表）



##### 类功能：

Directory：维护所有服务提供者信息

RegistryDirectory：注册中心提供Directory  - 持有 ZookeeperRegistry 和 Urls（queryMap），订阅（subscirbe），监听节点变更时间 （notify）

RegistryProtocol：注册协议