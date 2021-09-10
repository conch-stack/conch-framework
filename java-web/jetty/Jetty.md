## Jetty

![Jetty](assets/Jetty.png)

#### Connector

处理Http请求



#### Handler

处理Servlet



#### ThreadPool

Connector和Handler两个组件所需要的线程资源直接从一个全局的线程池里拿



#### Server

启动与协调核心组件工作

负责创建并初始化Connector、Handler、ThreadPool组件，然后调用start方法启动它们





#### 对比Tomcat

整体架构相似

- 区别一：Jetty中无 Service 概念
  - Tomcat中的Service包装了多个连接器和一个容器组件，一个Tomcat实例可以配置多个Service，不同的Service通过不同的连接器监听不同的端口
  - Jetty中Connector是被所有Handler共享的
- 区别二：线程池不同
  - Tomcat中每个连接器都有自己的线程池
    - Jetty中所有的Connector共享一个全局的线程池