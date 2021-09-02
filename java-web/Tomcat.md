## Tomcat

![Tomcat](./assets/Tomcat.png)



- Http服务
  - 和客户端浏览器进行交互，进行socket通信，将字节流和Request/Response等对象进行转换
  - Connector（Coyote）：连接器组件
- Servlet容器，实现Servlet规范
  - 处理业务逻辑
  - Container（Catalina）：容器组件
- 其他
  - Jasper：JSP引擎
  - JaveEL：表达式语言
  - Naming：提供JNDI服务
  - Juli：服务器日志



#### 连接器（Coyote）

Coyote 是Tomcat 中连接器的组件名称 , 是对外的接口。客户端通过Coyote与服务器建立连接、发送请求并接受响应。

- Coyote 封装了底层的网络通信（Socket 请求及响应处理）
- Coyote 使Catalina 容器（容器组件）与具体的请求协议及IO操作方式完全解耦-
- Coyote 将Socket 输入转换封装为 Request 对象，进一步封装后交由Catalina 容器进行处理，处理请求完成后, Catalina 通过Coyote 提供的Response 对象将结果写入输出流
- Coyote 负责的是具体协议（应用层）和IO（传输层）相关内容、



**核心组件：**

- **EndPoint**：是Coyote通信监听接口，是具体的Socket接收发送处理器，是用来实现TCP/IP协议的，是对传输层的抽象。

- **Processor**：是Coyote协议处理接口，用来接收EndPoint的Socket，读取字节流解析成Tomcat Request和Response对象，并通过Adapter提交到对应容器处理，是用来实现HTTP协议的，是对应用层的抽象。

  - **ProtocolHandler**：Coyote 协议接口，通过Endpoint和Processor组件，实现针对具体协议的处理能力。Tomcat 按照协议和I/O 提供了6个实现类 : AjpNioProtocol，AjpAprProtocol，AjpNio2Protocol，**Http11NioProtocol**，**Http11Nio2Protocol**，Http11AprProtocol

- **Adapter**：由于协议不同，客户端发过来的请求信息格式也不相同。ProtocolHandler接口解析请求并生成Tomcat Request类。由于不同协议导致的Request差异，Tomcat设计者的解决方案是引入**CoyoteAdapter**，连接器调用CoyoteAdapter的Sevice方法，传入Tomcat Request对象， CoyoteAdapter负责将Tomcat Request转成ServletRequest，再调用容器。

  



#### Servlet容器（Catalina）

server.xml解析：

- Catalina
  - Server
    - Service
      - Connector



**核心组件：**

- **Engine**：Servlet引擎，用来管理多个虚拟站点，一个Service最多只能有一个Engine， 但是一个Engine可包含多个Host。
- **Host**：代表一个虚拟主机，可以给Tomcat配置多个虚拟主机地址，而一个虚拟主机下可包含多个Context.
- **Context**：表示一个Web应用程序， 一个Web应用可包含多个Wrapper
- **Wrapper**：表示一个Servlet，Wrapper 作为容器中的最底层，不能包含子容器









**ServletContainerInitializer**

StandardContext（引擎上下文）：Catalina主要包括Connector和Container，StandardContext就是一个Container

StandardContextValve

ApplicationContextFacade -> 实现自 ServletContext，Facade对象，它从web应用程序屏蔽内部ApplicationContext对象。



org.apache.coyote.Request -> Tomcat原始Request，会使用适配器适配到 ServletRequest

org.apache.coyote.Response



ApplicationHttpRequest



org.apache.catalina.core.ApplicationFilterChain

org.apache.catalina.connector.RequestFacade 实现自：HttpServletRequest

org.apache.catalina.connector.ResponseFacade

Filter

FilterChain

LifecycleMBeanBase