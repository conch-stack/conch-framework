## Tomcat源码分析

#### Catalina

Catalina的主要任务就是创建Server，解析server.xml，把在server.xml里配置的各种组件一一创建出来，接着调用Server组件的init方法和start方法，这样整个Tomcat就启动起来了

- 优雅的停止并且清理资源（在JVM中注册一个“关闭钩子”）

```java
/* Catalina  */

// Server.start()
public void start() {
    //1. 如果持有的Server实例为空，就解析server.xml创建出来
    if (getServer() == null) {
        load();
    }
    //2. 如果创建失败，报错退出
    if (getServer() == null) {
        log.fatal(sm.getString("catalina.noServer"));
        return;
    }

    //3.启动Server
    try {
        getServer().start();
    } catch (LifecycleException e) {
        return;
    }

    //创建并注册关闭钩子
    if (useShutdownHook) {
        if (shutdownHook == null) {
            shutdownHook = new CatalinaShutdownHook();
        }
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    //用await方法监听停止请求 - 整个容器都hold住
    if (await) {
        await();
        stop();
    }
}

// CatalinaShutdownHook
protected class CatalinaShutdownHook extends Thread {

    @Override
    public void run() {
        try {
            if (getServer() != null) {
                // 执行了Server的stop方法，Server的stop方法会释放和清理所有的资源。
                Catalina.this.stop();
            }
        } catch (Throwable ex) {
           ...
        }
    }
}
```



#### Server

StandardServer -> Server

StandardServer 组合了 Service子组件，用数组存储，节约内存，维护Service子组件的生命周期

```java
/* StandardServer  */

@Override
public void addService(Service service) {

    service.setServer(this);

    synchronized (servicesLock) {
        //创建一个长度+1的新数组
        Service results[] = new Service[services.length + 1];
        
        //将老的数据复制过去
        System.arraycopy(services, 0, results, 0, services.length);
        results[services.length] = service;
        services = results;

        //启动Service组件
        if (getState().isAvailable()) {
            try {
                service.start();
            } catch (LifecycleException e) {
                // Ignore
            }
        }

        //触发监听事件
        support.firePropertyChange("service", null, service);
    }

}
```



#### Service

StandardService -> Service

```java
/* StandardService */

public class StandardService extends LifecycleBase implements Service {
    //名字
    private String name = null;
    
    //Server实例
    private Server server = null;

    //连接器数组
    protected Connector connectors[] = new Connector[0];
    private final Object connectorsLock = new Object();

    //对应的Engine容器
    private Engine engine = null;
    
    //映射器及其监听器
    protected final Mapper mapper = new Mapper();
    protected final MapperListener mapperListener = new MapperListener(this);
```

从上面的属性就能看出，Service的架构情况。

**MapperListener**：因为Tomcat支持热部署，当Web应用的部署发生变化时，Mapper中的映射信息也要跟着变化，MapperListener就是一个监听器，它监听容器的变化，并把信息更新到Mapper中，这是典型的观察者模式

启动：

```java
/* StandardService */

protected void startInternal() throws LifecycleException {

    //1. 触发启动监听器
    setState(LifecycleState.STARTING);

    //2. 先启动Engine，Engine会启动它子容器
    if (engine != null) {
        synchronized (engine) {
            engine.start();
        }
    }
    
    //3. 再启动Mapper监听器
    mapperListener.start();

    //4.最后启动连接器，连接器会启动它子组件，比如Endpoint
    synchronized (connectorsLock) {
        for (Connector connector: connectors) {
            if (connector.getState() != LifecycleState.FAILED) {
                connector.start();
            }
        }
    }
}
```



#### Engine

StandardEngine -> ContainerBase, Engine

Engine本质就是一个容器

```java
/* ContainerBase */

// Engine子容器 - Host（也是容器）- 这个数据结构被抽象到了 ContainerBase 中，提供统一的“增删改查”
protected final HashMap<String, Container> children = new HashMap<>();

// 甚至子组件的启动停止都有提供默认实现
for (int i = 0; i < children.length; i++) {
   results.add(startStopExecutor.submit(new StartChild(children[i])));
}
```

那Engine自己做了什么呢？我们知道容器组件最重要的功能是处理请求，而Engine容器对请求的“处理”，其实就是把请求转发给某一个Host子容器来处理，具体是通过Valve来实现的。

Engine的Pipeline中有一个基础阀（Basic Valve），他会调用子容器Host的Pipline的第一个Valve

```java
final class StandardEngineValve extends ValveBase {

    public final void invoke(Request request, Response response)
      throws IOException, ServletException {
  
      // 拿到请求中的Host容器
      // 因为请求到达Engine容器中之前，Mapper组件已经对请求进行了路由处理，Mapper组件通过请求的URL定位了相应的容器，并且把容器对象保存到了请求对象中
      Host host = request.getHost();
      if (host == null) {
          return;
      }
  
      // 调用Host容器中的Pipeline中的第一个Valve
      host.getPipeline().getFirst().invoke(request, response);
  }
  
}
```

