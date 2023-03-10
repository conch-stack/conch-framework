## Tomcat源码分析（六）



### 类加载器

Java中的类遵循按需加载。

所谓类加载器，就是⽤于加载 Java 类到 Java 虚拟机中的组件，它负责读取 Java 字节码，并转换成java.lang.Class 类的⼀个实例，使字节码.class ⽂件得以运⾏。⼀般类加载器负责根据⼀个指定的类找到对应的字节码，然后根据这些字节码定义⼀个 Java 类。另外，它还可以加载资源，包括图像⽂件和配置
⽂件。

类加载器在实际使⽤中给我们带来的好处是，它可以使 Java 类动态地加载到 JVM 中并运⾏，即可在程序运⾏时再加载类，提供了很灵活的动态加载⽅式。

- 启动类加载器（Bootstrap ClassLoader）：加载对象是 Java 核⼼库，把⼀些核⼼的 Java 类加载进JVM 中，这个加载器使⽤原⽣代码（C/C++）实现，并不是继承 java.lang.ClassLoader，它是所有其他类加载器的最终⽗加载器，负责加载 <JAVA_HOME>/jre/lib ⽬录下 JVM 指定的类库。其实它属于 JVM 整体的⼀部分，JVM ⼀启动就将这些指定的类加载到内存中，避免以后过多的 I/O 操作，提⾼系统的运⾏效率。启动类加载器⽆法被 Java 程序直接使⽤。
- 扩展类加载器（Extension ClassLoader）：加载的对象为 Java 的扩展库，即加载<JAVA_HOME>/jre/lib/ext ⽬录⾥⾯的类。这个类由启动类加载器加载，但因为启动类加载器并⾮⽤ Java 实现，已经脱离了 Java 体系，所以如果尝试调⽤扩展类加载器的 getParent()⽅法获取⽗加载器会得到 null。然⽽，它的⽗类加载器是启动类加载器。
- 应⽤程序类加载器（Application ClassLoader）：亦叫系统类加载器（System ClassLoader），它负责加载⽤户类路径（CLASSPATH）指定的类库，如果程序没有⾃⼰定义类加载器，就默认使⽤应⽤程序类加载器。它也由启动类加载器加载，但它的⽗加载类被设置成了扩展类加载器。如果要使⽤这个加载器，可通过 ClassLoader.getSystemClassLoader()获取。



### 双亲委派

双亲委派模型会在类加载器加载类时⾸先委托给⽗类加载器加载，除⾮⽗类加载器不能加载才⾃⼰加载。

这种模型要求，除了顶层的启动类加载器外，其他的类加载器都要有⾃⼰的⽗类加载器。假如有⼀个类要加载进来，⼀个类加载器并不会⻢上尝试⾃⼰将其加载，⽽是委派给⽗类加载器，⽗类加载器收到后⼜尝试委派给其⽗类加载器，以此类推，直到委派给启动类加载器，这样⼀层⼀层往上委派。只有当⽗类加载器反馈⾃⼰没法完成这个加载时，⼦加载器才会尝试⾃⼰加载。通过这个机制，保证了 Java 应⽤所使⽤的都是同⼀个版本的 Java 核⼼库的类，同时这个机制也保证了安全性。设想如果应⽤程序类加载器想要加载⼀个有破坏性的 java.lang.System 类，双亲委派模型会⼀层层向上委派，最终委派给启动类加载器，⽽启动类加载器检查到缓存中已经有了这个类，并不会再加载这个有破坏性的 System 类。

另外，类加载器还拥有全盘负责机制，即当⼀个类加载器加载⼀个类时，这个类所依赖的、引⽤的其他所有类都由这个类加载器加载，除⾮在程序中显式地指定另外⼀个类加载器加载。

在 Java 中，我们⽤完全匹配类名来标识⼀个类，即⽤包名和类名。⽽在 JVM 中，⼀个类由完全匹配类名和⼀个类加载器的实例 ID 作为唯⼀标识。也就是说，同⼀个虚拟机可以有两个包名、类名都相同的类，只要它们由两个不同的类加载器加载。当我们在 Java 中说两个类是否相等时，必须在针对同⼀个类加载器加载的前提下才有意义，否则，就算是同样的字节码，由不同的类加载器加载，这两个类也不是相等的。这种特征为我们提供了隔离机制，在 Tomcat 服务器中它是⼗分有⽤的。



### URLClassLoader

我们在使⽤⾃定义类加载去加载类时，我们需要指明该去哪些资源中进⾏加载，所以JDK提供了URLClassLoader来⽅便我们使⽤，我们在创建URLClassLoader时需要传⼊⼀些URLs，然后在使⽤这个URLClassLoader加载类时就会从这些资源中去加载。



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

Tomcat 拥有不同的⾃定义类加载器，以实现对各种资源库的控制。⼀般来说，Tomcat 主要⽤类加载器
解决以下 4 个问题。

- 同⼀个Tomcat中，各个Web应⽤之间各⾃使⽤的Java类库要互相隔离。
- 同⼀个Tomcat中，各个Web应⽤之间可以提供共享的Java类库。
- 为了使Tomcat不受Web应⽤的影响，应该使服务器的类库与应⽤程序的类库互相独⽴。
- Tomcat⽀持热部署。



##### CommonClassLoader

它的⽗类加载器是应⽤程序类加载器，负责加载 $CATALINA_ BASE/lib、$CATALINA_HOME/lib 两个⽬录下所有的.class ⽂件与.jar ⽂件。

Common 类加载器的存在使多个 Web 应⽤程序能够互相共享类库。



**WebappClassLoader **：

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