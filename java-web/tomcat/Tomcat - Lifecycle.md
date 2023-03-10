## Lifecycle

> 参考：ltd.beihu.sample.lifecycle.Lifecycle



### 生命周期流转图

![image-20230310204651420](.//assets/image-20230310204651420.png)

- 所有状态都能转变为FAILED
- ⼀个组件在STARTING_PREP、STARTING、STARTED状态调⽤start()⽅法不会产⽣影响
- ⼀个组件在NEW状态调⽤start()⽅法时，会先调⽤init()⽅法
- ⼀个组件在STOPPING_PREP、STOPPING、STOPPED状态调⽤stop⽅法不会产⽣影响
- ⼀个组件在NEW状态调⽤stop()方法时，会将状态直接改为STOPPED。当组件⾃⼰启动失败去停⽌时，需要将⼦组件也进⾏停⽌，尽管某些⼦组件还没有启动。
- 其他状态相互转换都会抛异常
- 合法的状态转换发⽣时都会触发相应的LifecycleEvent事件，⾮合法的转换不会触发事件。



### 事件监听

##### 事件触发：

Tomcat中每个组件的状态会发送变化，变化的时候会抛出⼀些事件，Tomcat⽀持定义事件监听器来监听并消费这些事件。

##### 事件执行：

实现事件监听功能的类为org.apache.catalina.util.LifecycleBase。每个组件都会继承这个类。

该类中有⼀个属性： **List\<LifecycleListener> lifecycleListeners ;** 该属性⽤来保存事件监听器，也就是说每个组件拥有⼀个事件监听器列表。

```java
/**
 * Allow sub classes to fire {@link Lifecycle} events.
 *
 * @param type  Event type
 * @param data  Data associated with event.
 */
protected void fireLifecycleEvent(String type, Object data) {
    LifecycleEvent event = new LifecycleEvent(this, type, data);
    for (LifecycleListener listener : lifecycleListeners) {
        listener.lifecycleEvent(event);
    }
}
```

当组件的状态发⽣变化时，会调⽤fireLifecycleEvent触发事件执⾏。⽐如当Server初始化时，会调⽤：LifecycleBase.start()；start() 第一次调用又会先触发init()

```java
public final synchronized void init() throws LifecycleException {
    // ...
    setStateInternal(LifecycleState.INITIALIZING, null, false);
    initInternal();
    setStateInternal(LifecycleState.INITIALIZED, null, false);
    // ...
}

public final synchronized void start() throws LifecycleException {
    // ...
	setStateInternal(LifecycleState.STARTING_PREP, null, false);
	startInternal();
    // ...
}

private synchronized void setStateInternal(LifecycleState state, Object data, boolean check) throws LifecycleException {
    // ...
    this.state = state;
    String lifecycleEvent = state.getLifecycleEvent();
    if (lifecycleEvent != null) {
        // 触发事件监听器执行
        fireLifecycleEvent(lifecycleEvent, data);
    }
    // ...
}

// 留给子类去定制实现自己业务逻辑
protected abstract void initInternal() throws LifecycleException;
protected abstract void startInternal() throws LifecycleException;
```

- setStateInternal(xxx)： 为LifecycleBase内部私有方法，专门处理组件 LifecycleBase 在启动初期的 状态流转，及触发该状态的事件处理

- startInternal() ：为抽象方法，留个各子组件，进行扩展，子组件可在内部进行逻辑处理，并设置其生命周期状态state：

  ```java
  // 例：org.apache.catalina.core.ContainerBase
  protected synchronized void startInternal() throws LifecycleException {
      // ...
      Cluster cluster = getClusterInternal();
      // ...
      Realm realm = getRealmInternal();
      // ...
      // Start our child containers, if any
      // 启动所有的子容器
      Container children[] = findChildren();
      List<Future<Void>> results = new ArrayList<>();
      for (Container child : children) {
          results.add(startStopExecutor.submit(new StartChild(child)));
      }
      // ...
      for (Future<Void> result : results) {
          try {
              result.get();
          } catch (Throwable e) {
              // ...
          }
  
      }
       // ...
  
      // Start the Valves in our pipeline (including the basic), if any
      // 启动所有的 pipeline
      if (pipeline instanceof Lifecycle) {
          ((Lifecycle) pipeline).start();
      }
  
      // 设置容器状态为 LifecycleState.STARTING
      setState(LifecycleState.STARTING);
  
      // Start our thread
      // 启动后台监听线程
      if (backgroundProcessorDelay > 0) {
          monitorFuture = Container.getService(ContainerBase.this).getServer()
                  .getUtilityExecutor().scheduleWithFixedDelay(
                          new ContainerBackgroundProcessorMonitor(), 0, 60, TimeUnit.SECONDS);
      }
  }
  
  // org.apache.catalina.util.LifecycleBase
  protected synchronized void setState(LifecycleState state) throws LifecycleException {
      // 复用了
      setStateInternal(state, null, true);
  }
  ```



##### 事件监听器

程序员可以⾃定义事件监听器，只需实现LifecycleListener接⼝即可；

定义好事件监听器后，每个组件就可以调⽤⽗类LifecycleBase中的addLifecycleListener()⽅法添加事件
监听器到该组件的监听器列表中。

##### 总结

虽然说是事件监听，但实际上并不是异步触发，⽽是主动调⽤事件监听器。



### 容器初始化

<img src="./assets/image-20230310224510404.png" alt="image-20230310224510404" style="zoom:80%;" />

根据初始化过程对Tomcat源码分析，其处理步骤如下：

1. 调用放调用父类LifecycleBase的init方法，LifecycleBase的init方法主要完成一些所有容器公共抽象出来的动作
2. LifecycleBase的init方法调由具体容器的initInternal方法实现，initInternal方法用于对容器本身真正的初始化
3. 具体容器的initInternal方法调用父类LifecycleMBeanBbase的initInternal方法实现，此initIneral方法用于将容器托管到JMX，便于运维管理
4. LifecycleMBeanBase的initInternal方法调用自身的register方法，将容器作为MBean方法注册到MBeanServer
5. 容器如果有子容器，会调用子容器的init方法
6. 容器初始化完毕，LifecycleBase会将容器的状态更改为初始化完毕，即`LifecycleState.INITIALIZED`



### 容器启动

<img src="./assets/image-20230310224620328.png" alt="image-20230310224620328" style="zoom:80%;" />

根据对源码的分析，其处理步骤如下：

1. 调用方调用容器父类LifecycleBase的start方法，LifecycleBase的start方法主要完成一些容器公共抽象出来的动作
2. LifecycleBase的start方法先将容器状态改为`LifecycleState.STARTING_PREP`，然后调用具体容器的startInternal方法实现，此startInternal方法用于对容器本身真正的初始化
3. 具体容器的startInternal方法会将容器状态改为`LifecycleState.STARTING`，容器如果有子容器，会调用子容器的start方法启动子容器
4. 容器启动完毕，LifecycleBase会将容器的状态更改为启动完毕，即`LifecycleState.STARTED`



##### StandardContext 

// 重点：StandardContext 对应一个Web应用？ TODO 去查看Tomcat的配置文件
StandardContext 会将 WebappClassLoaderBase 进行 绑定操作，是为了干什么？ 



##### WebappClassLoader -> WebappClassLoaderBase -> URLClassLoader

将/WEB-INF/classes和/WEB-INF/lib⽬录作为loaderRepositories，后⾯应⽤如果加载类就从这两个⽬录加载

```java
// org.apache.catalina.core.StandardContext.startInternal()
// Tomcat上下文启动的时候，会创建WebappLoader
// ...
if (getLoader() == null) {
    WebappLoader webappLoader = new WebappLoader();
    webappLoader.setDelegate(getDelegate());
    setLoader(webappLoader);
}
// ...
Loader loader = getLoader();
if (loader instanceof Lifecycle) {
    ((Lifecycle) loader).start();
}

// WebappLoader内部持有 parentClassLoader 和 WebappClassLoaderBase
// WebappLoader.startInternal() 启动的时候，会创建 WebappClassLoaderBase
private ClassLoader parentClassLoader = null;
private WebappClassLoaderBase classLoader = null;
// ...
WebappClassLoaderBase classLoader = createClassLoader();
classLoader.start(); // 

// WebappClassLoaderBase.start() 会加载 /WEB-INF/classes和/WEB-INF/lib 作为loaderRepositories
public void start() throws LifecycleException {
    state = LifecycleState.STARTING_PREP;

    WebResource[] classesResources = resources.getResources("/WEB-INF/classes");
    for (WebResource classes : classesResources) {
        if (classes.isDirectory() && classes.canRead()) {
            localRepositories.add(classes.getURL());
        }
    }
    WebResource[] jars = resources.listResources("/WEB-INF/lib");
    for (WebResource jar : jars) {
        if (jar.getName().endsWith(".jar") && jar.isFile() && jar.canRead()) {
            localRepositories.add(jar.getURL());
            jarModificationTimes.put(
                    jar.getName(), Long.valueOf(jar.getLastModified()));
        }
    }

    state = LifecycleState.STARTED;
}
```



##### StandardService

org.apache.catalina.core.StandardService.startInternal() 时

- 启动engine
- 启动executor
- 启动mapperListener
- 启动所有connector

**也就是说，生命周期的管理，从StandardServer开始(当然他上层还有其他组件)，就是一层嵌套一层的进行管理的**

**每一层有可以包含多个相同类型或不同类型的子组件**

**以：StandardService 为例**

- StandardService .start()
  - StandardService.startInternal()
    - StandardEngine.start()
      - StandardEngine.startInternal()
    - Connector.start()
      - Connector.startInternal()
    - StandardThreadExecutor.start()
      - StandardThreadExecutor.startInternal()
- StandardService .stop()
  - StandardService.stopInternal()
    - StandardEngine.stop()
      - StandardEngine.stopInternal()

    - Connector.pause();  // 特殊处理的，优雅关闭链接后（ProtocolHandler实现的）
    - Connector.stop()
      - Connector.stopInternal()
    - StandardThreadExecutor.stop()
      - StandardThreadExecutor.stopInternal()




```java
protected void startInternal() throws LifecycleException {

    if(log.isInfoEnabled())
        log.info(sm.getString("standardService.start.name", this.name));
    setState(LifecycleState.STARTING);

    // Start our defined Container first
    if (engine != null) {
        synchronized (engine) {
            engine.start();
        }
    }

    synchronized (executors) {
        for (Executor executor: executors) {
            executor.start();
        }
    }

    mapperListener.start();

    // Start our defined Connectors second
    synchronized (connectorsLock) {
        for (Connector connector: connectors) {
            // If it has already failed, don't try and start it
            if (connector.getState() != LifecycleState.FAILED) {
                connector.start();
            }
        }
    }
}
```



### Await

Tomcat的Catalina和Server提供了await() 方法，为了让容器启动后可以一直运行

方式一：Tomcat.main() 启动容器（Main executable method for use with a Maven packager）

```java
// 在Maven启动方式中，Tomcat启动会利用 Catalina这个工具人 包装一层 真正的 Server
// Catalina 中主要处理一下Java -jar启动的时候一些命令解析；xml文件解析，配置文件解析
tomcat.init(null, catalinaArguments);  // 利用 Catalina 创建 Server

// 启动Tomcat
tomcat.start();
// Ideally the utility threads are non daemon
// 等待结束，这边推荐不要使用守护线程
if (await) {
    // Wait until a proper shutdown command is received, then return.
    // This keeps the main thread alive - the thread pool listening for http
    // connections is daemon threads
    tomcat.getServer().await();
}
```

方式二：org.apache.catalina.startup.Bootstrap（Main method and entry point when starting Tomcat via the provided scripts）

```java
// 在Tomcat提供的脚步启动方式中使用
// 同样会使用 Catalina 进行 init()
Bootstrap.main() -> init()
// 里面都是反射调用 Catalina
```

```java
if (command.equals("start")) {
	// 等待
	daemon.setAwait(true);
	daemon.load(args);
	daemon.start();
}
```

方式三：SpringBoot

org.springframework.boot.web.embedded.tomcat.TomcatWebServer

```java
// new TomcatWebServer() -> this.initialize()
private void initialize() throws WebServerException {
    logger.info("Tomcat initialized with port(s): " + getPortsDescription(false));
    synchronized (this.monitor) {
        try {
            // ... 获取Tomcat上下文
            // ... 加入Spring的特殊生命周期监听器
            
            
            // Start the server to trigger initialization listeners
            // 启动
            this.tomcat.start();

     		// ...
            
            // Unlike Jetty, all Tomcat threads are daemon threads. We create a
            // blocking non-daemon to stop immediate shutdown
            // 所有的Tomcat内部线程都是守护线程，需要利用非守护线程来响应 immediate shutdown
            startDaemonAwaitThread();
        }
        catch (Exception ex) {
            stopSilently();
            destroySilently();
            throw new WebServerException("Unable to start embedded Tomcat", ex);
        }
    }
}

// 非Daemon
// SpringBoot启动完成后，其主线程是不存活的，而是依靠上面提供的非Daemon线程对Tomcat的Server的await()的等待，维持整个容器的运行。
private void startDaemonAwaitThread() {
   Thread awaitThread = new Thread("container-" + (containerCounter.get())) {

      @Override
      public void run() {
         TomcatWebServer.this.tomcat.getServer().await();
      }

   };
   awaitThread.setContextClassLoader(getClass().getClassLoader());
   //非Daemon
   awaitThread.setDaemon(false);
   awaitThread.start();
}
```

**SpringBoot启动完成后，其主线程是不存活的，而是依靠上面提供的非Daemon线程对Tomcat的Server的await()的等待，维持整个容器的运行。**

你可以在IDEA的线程窗口看到以 “container-XXX”  为名称的线程



##### 补充：StandardServer.await() 核心逻辑

即“**一直等待到接收到一个正确的关闭命令后该方法将会返回。这样会使主线程一直存活——监听http连接的线程池是守护线程**”。

熟悉 Java 的 Socket 编程的话对这段代码就很容易理解，就是默认地址（地址值由实例变量 address 定义，默认为`localhost`）的默认的端口（端口值由实例变量 port 定义，默认为`8005`）上监听 Socket 连接，当发现监听到的连接的输入流中的内容与默认配置的值匹配（该值默认为字符串`SHUTDOWN`）则跳出循环，该方法返回（第 103 到 107 行）。否则该方法会一直循环执行下去。 一般来说该用户主线程会阻塞（第 56 行）直到有访问`localhost:8005`的连接出现。 正因为如此才出现开头看见的主线程一直 Running 的情况，而因为这个线程一直 Running ，其它守护线程也会一直存在。

说完这个线程的产生，接下来看看这个线程的关闭，按照上面的分析，这个线程提供了一个关闭机制，即只要访问`localhost:8005`，并且发送一个内容为`SHUTDOWN`的字符串，就可以关闭它了。

Tomcat 正是这么做的，一般来说关闭 Tomcat 通过执行 shutdown.bat 或 shutdown.sh 脚本，



##### 补充：Daemon线程

Java中有两类线程：User Thread(用户线程)、Daemon Thread(守护线程)；

 Daemon的作用是为其他线程的运行提供服务，比如说GC线程。其实User Thread线程和Daemon Thread守护线程本质上来说去没啥区别的，唯一的区别之处就在虚拟机的离开：如果User Thread全部撤离，那么Daemon Thread也就没啥线程好服务的了，所以虚拟机也就退出了。

**Tomcat的关闭正是利用了这个原理，即只要将那唯一的一个用户线程关闭，则整个应用就关闭了。**  -- 网上描述有误，看源码更正

- Tomcat后台实际上总共有 6 个线程在运行。即 1 个用户线程，剩下 5 个为守护线程(下图中的 Daemon Thread )

**更正：**

**默认情况下，Tomcat的Executor自定义线程池都是守护线程**

- Executor -> StandardThreadExecutor -> **protected boolean daemon = true;**

默认情况下，Tomcat的 utilityExecutor 工具线程池是 **非守护线程**

- StandardServer -> ScheduledThreadPoolExecutor utilityExecutor -> **protected boolean utilityThreadsAsDaemon = false;**
- utilityExecutor 线程池：主要是干杂活，比如在后台定期检查Session是否过期、定期检查Web应用是否更新（热部署热加载）、检查异步Servlet的连接是否过期等等，线程名 “Catalina-utility-xxx“
- 这个线程池会限制只有2个线程



#### GracefulShutdown

SpringBoot为了嵌入式的Tomcat支持的优雅关闭



### 特殊事件

##### PERIODIC_EVENT

StandardServer.startInternal() 最后，会触发 异步线程，定时触发 Lifecycle.PERIODIC_EVENT 事件

org.apache.catalina.startup.HostConfig 会对该事件进行处理

```java
public void lifecycleEvent(LifecycleEvent event) {
	//...
    // Process the event that has occurred
    if (event.getType().equals(Lifecycle.PERIODIC_EVENT)) {
        check();
    } else if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
        beforeStart();
    } else if (event.getType().equals(Lifecycle.START_EVENT)) {
        start();
    } else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
        stop();
    }
}
```

- check()方法响应PERIODIC_EVENT周期性检查事件，主要是检查资源是否被更改，如果被更改就会重新解压、重新加载。
- beforeStart()响应BEFORE_START_EVENT状态，主要是设置资源目录等相关配置，为START_EVENT做准备。
- start()响应START_EVENT状态，这里就是启动是读取配置、解压包、重新加载资源的操作。下面也主要以这个方法为例。

##### HostConfig

HostConfig是Host容器的状态监听器

Host这个层次做WAR解压和部署，因为Context和Wrapper是动态添加的，我们在tomcat的指定目录下每添加一个war包，tomcat加载war包时，就可以添加Context和Servlet

- deployWARs()：异步触发过个War的解压配置部署等工作
- deployWAR()：真正进行动态部署
  - war包解析，解压缩
  - 配置文件读取
  - Context类加载，类配置。可以是标准StandardContext，也可以是war中自己定义的。
  - war中自定义的状态监听器的加载。
  - Host使用addChild将context实例加入到tomcat的Host配置中。
  - **对解压缩的war包进行监控，发生变化就会触发上面说到的check()**
