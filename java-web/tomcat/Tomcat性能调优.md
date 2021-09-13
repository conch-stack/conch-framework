## Tomcat性能调优

> 如果你是用嵌入式的方式运行Tomcat，比如Spring Boot，你也可以**通过Spring Boot的方式去修改Tomcat的参数**，调优的原理都是一样的



##### 异步Servlet：

业务处理时间过长，导致Tomcat线程饥饿，可以考试使用异步Servlet，这样Tomcat线程立刻返回，耗时处理由业务线程来处理；

如果业务线程出现阻塞，比如**I/O密集型**，这时考试使用 “异步回调”来避免阻塞，使用异步非阻塞IO模型，用少量线程通过事件循环来提高吞吐量，Spring给到的方案是：Webflux



##### 线程数量设置：

线程数具体设多少，根据具体业务而定，如果你的业务是IO密集型的，比如大量线程等待在数据库读写上，线程数应该设的越高。如果是CPU密集型，完全没有阻塞，设成CPU核数就行

理论上：

线程数=（(线程阻塞时间 + 线程忙绿时间) / 线程忙碌时间) * cpu核数

如果线程始终不阻塞，一直忙碌，会一直占用一个CPU核，因此可以直接设置 线程数=CPU核数。

但是现实中线程可能会被阻塞，比如等待IO。因此根据上面的公式确定线程数。

那怎么确定线程的忙碌时间和阻塞时间？要经过压测，在代码中埋点统计，***TODO***



##### 上下文切换：

线程阻塞会发生线程上下文切换，浪费CPU资源

- 上下文切换（Context Switch）：一个线程被暂停剥夺使用权，另一个线程被选中开始或者继续运行的过程
  切出：一个线程被剥夺处理器的使用权而被暂停运行
  切入：一个线程被选中占用处理器开始运行或者继续运行
  切出切入的过程中，操作系统需要保存和恢复相应的进度信息，这个进度信息就是上下文



##### 禁用Tomcat TLD扫描：

Tomcat为了支持JSP，在应用启动的时候会扫描JAR包里面的TLD文件，加载里面定义的标签库



##### 关闭WebSocket支持：

context.xml中的 **containerSciFilter** 加上WsSci



##### 关闭JSP支持：

context.xml中的 **containerSciFilter** 加上JasperInitializer



##### 禁止Servlet注解扫描：

在你的Web应用的`web.xml`文件中，设置`<web-app>`元素的属性`metadata-complete="true"`

`metadata-complete`的意思是，`web.xml`里配置的Servlet是完整的，不需要再去库类中找Servlet的定义



##### 配置Web-Fragment扫描：

Servlet 3.0还引入了“Web模块部署描述符片段”的`web-fragment.xml`，这是一个部署描述文件，可以完成`web.xml`的配置功能。

通过配置`web.xml`里面的`<absolute-ordering>`元素直接指定了哪些JAR包需要扫描`web fragment`，如果`<absolute-ordering/>`元素是空的， 则表示不需要扫描



##### 随机数熵源优化：

JVM 默认使用阻塞式熵源（`/dev/random`）在某些情况下就会导致Tomcat启动变慢。

解决方案是通过设置，让JVM使用非阻塞式的熵源：

- 设置JVM的参数：-Djava.security.egd=file:/dev/./urandom



##### 去掉access log：

因为nginx里已有access log
