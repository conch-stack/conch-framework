## Tomcat源码分析（六）



### Tomcat热加载、热部署

解决方案：后台异步线程定时检测变化 或 发布事件监听处理

ContainerBase中设计了 `backgroundProcess()`方法，意味着每个容器组件都可以有自己的后台处理任务



##### 热加载

Context -> StandardContext#backgroundProcess()

##### 热部署

HostConfig#lifecycleEvent(LifecycleEvent event)





### Tomcat ClassLoader设计

<img src="assets/image-20210922185318770.png" alt="image-20210922185318770" style="zoom:50%;" />