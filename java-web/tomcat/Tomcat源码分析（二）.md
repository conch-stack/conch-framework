## Tomcat源码分析（二）



### 对象池技术

一个请求到来，Tomcat会创建一个SocketProccessor，这是个runnable，会被扔到线程池执行。

在执行过程中，会创建一个Request对象，一个Response对象，和一个Http11Processor对象（AbstractProcessor的子类）。
一次请求处理完了，这些对象会被回收保存起来，重复使用（对象池技术）。

