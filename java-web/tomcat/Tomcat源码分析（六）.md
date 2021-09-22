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

##### 定制ClassLoader

- **WebappClassLoader **：
  - **findClass**:
    - 先在Web应用本地目录下查找要加载的类。
    - 如果没有找到，交给父加载器去查找，它的父加载器就是上面提到的系统类加载器AppClassLoader。
    - 如何父加载器也没找到这个类，抛出ClassNotFound异常。
  - **loadClass**:
    - 先在本地Cache查找该类是否已经加载过，也就是说Tomcat的类加载器是否已经加载过这个类。
    - 如果Tomcat类加载器没有加载过这个类，再看看系统类加载器是否加载过。
    - 如果都没有，就让**ExtClassLoader**去加载，这一步比较关键，目的**防止Web应用自己的类覆盖JRE的核心类**。因为Tomcat需要打破双亲委托机制，假如Web应用里自定义了一个叫Object的类，如果先加载这个Object类，就会覆盖JRE里面的那个Object类，这就是为什么Tomcat的类加载器会优先尝试用ExtClassLoader去加载，因为ExtClassLoader会委托给BootstrapClassLoader去加载，BootstrapClassLoader发现自己已经加载了Object类，直接返回给Tomcat的类加载器，这样Tomcat的类加载器就不会去加载Web应用下的Object类了，也就避免了覆盖JRE核心类的问题。
    - 如果ExtClassLoader加载器加载失败，也就是说JRE核心类中没有这类，那么就在本地Web应用目录下查找并加载。
    - 如果本地目录下没有这个类，说明不是Web应用自己定义的类，那么由系统类加载器去加载。这里请你注意，Web应用是通过`Class.forName`调用交给系统类加载器的，因为`Class.forName`的默认加载器就是系统类加载器。
    - 如果上述加载过程全部失败，抛出ClassNotFound异常。
- 解决类覆盖问题（打破双亲委派）
  - Tomcat的解决方案是自定义一个类加载器WebAppClassLoader， 并且给每个Web应用创建一个类加载器实例
- 解决类膨胀问题（SharedClassLoader共享库类）
  - Tomcat的设计者加了一个类加载器SharedClassLoader，作为WebAppClassLoader的父加载器，专门来加载Web应用之间共享的类。如果WebAppClassLoader自己没有加载到某个类，就会委托父加载器SharedClassLoader去加载这个类，SharedClassLoader会在指定目录下加载共享类，之后返回给WebAppClassLoader，这样共享的问题就解决了
- 实现隔离类访问问题（兄弟-类加载器）
  - **CatalinaClassloader** 与 **SharedClassLoader** 公用一个 **CommonClassLoader**，各自隔离，且公用 CommonClassLoader 的资源
  - CatalinaClassloader在Tomcat启动的时候放入了Tomcat启动线程的上下文中



##### Spring类加载实现 - 线程上下文加载器

Spring是通过调用`Class.forName`来加载业务类的

```java
public static Class<?> forName(String className) {
    Class<?> caller = Reflection.getCallerClass();
    return forName0(className, true, ClassLoader.getClassLoader(caller), caller);
}
```

可以看到在forName的函数里，会用**调用者**也就是**Spring的加载器**去加载业务类。

Web应用之间共享的JAR包可以交给SharedClassLoader来加载，从而避免重复加载。Spring作为共享的第三方JAR包，它本身是由SharedClassLoader来加载的，Spring又要去加载业务类，按照前面那条规则，加载Spring的类加载器也会用来加载业务类，但是业务类在Web应用目录下，不在SharedClassLoader的加载路径下，这该怎么办呢？

于是**线程上下文加载器**登场了，它其实是一种**类加载器传递机制**。为什么叫作“线程上下文加载器”呢，因为这个类加载器保存在线程私有数据里，只要是同一个线程，一旦设置了线程上下文加载器，在线程后续执行过程中就能把这个类加载器取出来用。**因此Tomcat为每个Web应用创建一个WebAppClassLoarder类加载器，并在启动Web应用的线程里设置线程上下文加载器，这样Spring在启动时就将线程上下文加载器取出来，用来加载Bean。**



##### FAQ

1. 在StandardContext的启动方法里，会将当前线程的上下文加载器设置为WebAppClassLoader。

```java
originalClassLoader = Thread.currentThread().getContextClassLoader();
Thread.currentThread().setContextClassLoader(webApplicationClassLoader);
```

在启动方法结束的时候，还会恢复线程的上下文加载器：

```java
Thread.currentThread().setContextClassLoader(originalClassLoader);
```

这是为什么呢？

答：**线程上下文加载器其实是线程的一个私有数据，跟线程绑定的，这个线程做完启动Context组件的事情后，会被回收到线程池，之后被用来做其他事情，为了不影响其他事情，需要恢复之前的线程上下文加载器。**