## MyBatis源码分析-基本篇（四）



##### 资源加载

ClassLoader基本知识：(双亲委派模型：简单来说就是子类必须将类的加载请求交给父类加载器先加载，当父加载器无法加载该类时，才交由子类继续加载)

-  Bootstrap ClassLoader：主要加载JDK自带的rt.jar包中的类，系统级别的，无法被Java程序获取，是所有类加载器的父加载器，且他自己无父来加载器
- Extension ClassLoader：主要加载  jre/lib/ext 目录或者 java.ext.dirs 属性所指定的目录 下的类（扩展类库）

- Application ClassLoader：（System ClassLoader）负责从 classpath中加载类（应用级别）
- 自定义 ClassLoader：实现自己的逻辑，可达到类隔离等作用
  - Tomcat会为每个部署的应用创建一个 WebApp ClassLoader，负责加载WEB-INF/lib目录下的jar文件以及WEB-INF/classes目录下的Class文件；
  - 热部署时，会抛弃旧的WebApp ClassLoader，创建新的WebApp ClassLoader
  - **WebApp ClassLoader 的父类加载器为 Tomcat定义的 Common ClassLoader，所有它可以使用Common ClassLoader加载的共享类库**



##### MyBatis的类加载器包装

- ClassLoaderWrapper：包装多个ClassLoader，对外只表现为一个ClassLoader在工作

  - ```java
    public class ClassLoaderWrapper {
      // TODO 默认类加载器
      ClassLoader defaultClassLoader;
      // TODO System ClassLoader (Application ClassLoader)
      ClassLoader systemClassLoader;
      // ...
    }
    ```

  - 按照指定顺序依次检测其中封装的ClassLoader对象，并选择一个可用的ClassLoader来完成相关功能

  - ```java
    /**
     * 包装多个ClassLoader，依次顺序迭代，获取资源
     * @param classLoader
     * @return
     */
    ClassLoader[] getClassLoaders(ClassLoader classLoader) {
      return new ClassLoader[]{
          classLoader,   // 指定的 ClassLoader
          defaultClassLoader,  // 默认 ClassLoader
          Thread.currentThread().getContextClassLoader(),  // 当前线程 Context ClassLoader
          getClass().getClassLoader(),  // 当前类的 ClassLoader
          systemClassLoader  // System ClassLoader
      };   
    }
    ```

  - 包装常用的一些资源加载方法

    - ```java
      /**
       * TODO 从当前Class Path中加载 资源
       */
      public URL getResourceAsURL(String resource)
      public URL getResourceAsURL(String resource, ClassLoader classLoader)
      public InputStream getResourceAsStream(String resource)
      public InputStream getResourceAsStream(String resource, ClassLoader classLoader) 
      public Class<?> classForName(String name)
      public Class<?> classForName(String name, ClassLoader classLoader) 
      ```

- Resources：内部组合了 ClassLoaderWrapper，并提供常用的资源加载方法，本质是调用ClassLoaderWrapper提供的功能

  - ```java
    /**
     * Returns a resource on the classpath as a Properties object
     */
    public static Properties getResourceAsProperties(String resource) throws IOException {
      Properties props = new Properties();
      try (InputStream in = getResourceAsStream(resource)) {
        props.load(in);
      }
      return props;
    }
    
    public static Reader getResourceAsReader(String resource)
    public static File getResourceAsFile(String resource)
    public static File getResourceAsFile(ClassLoader loader, String resource)
    ```



##### ResolverUtil：根据指定的条件在指定的类路径下查找类

- 组合 ClassLoader：记录当前的类加载器，默认为当前线程的上下文类加载器

- 条件定义接口：org.apache.ibatis.io.ResolverUtil.Test 

  - ```java
    /**
     * 定义条件规则
     */
    public interface Test {
      /**
       * 过滤类的接口方法，返回为True的才能够被 ResolverUtil 使用
       */
      boolean matches(Class<?> type);
    }
    ```

  - IsA：实现了 org.apache.ibatis.io.ResolverUtil.Test ，检测类是否继承了指定类或接口

    ```java
    public static class IsA implements Test {
      private Class<?> parent; // TODO 指定类
      
      public IsA(Class<?> parentType) {
        this.parent = parentType;
      }
    
      @Override
      public boolean matches(Class<?> type) { // TODO 目标 type
        return type != null && parent.isAssignableFrom(type);
      }
    }
    ```

  - AnnotatedWith：实现了 org.apache.ibatis.io.ResolverUtil.Test ，检测类是否添加了指定的注解

    ```java
    public static class AnnotatedWith implements Test {
      private Class<? extends Annotation> annotation;
    
      public AnnotatedWith(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
      }
    
      @Override
      public boolean matches(Class<?> type) {
        return type != null && type.isAnnotationPresent(annotation);
      }
    }
    ```

- 使用Demo

  ```java
  ResolverUtil<ActionBean> resolver = new ResolverUtil<ActionBean>();
  // 在 pkg1和pkg2下查找实现了 ActionBean 的类
  resolver.findImplementation(ActionBean.class, pkg1, pkg2);
  resolver.find(new CustomTest(), pkg1);  // pkg1包下查找符合CustomTest的类Class
  resolver.find(new CustomTest(), pkg2);
  Collectionz<ActionBean> beans = resolver.getClasses();
  ```

- find() 实现：

  ```java
  public ResolverUtil<T> find(Test test, String packageName) {
    String path = getPackagePath(packageName);
  
    try {
      // TODO 通过 VFS 获取包路径下的所有资源
      List<String> children = VFS.getInstance().list(path);
      for (String child : children) {
        if (child.endsWith(".class")) {
          addIfMatching(test, child);
        }
      }
    } catch (IOException ioe) {
      log.error("Could not read package: " + packageName, ioe);
    }
  
    return this;
  }
  
  protected void addIfMatching(Test test, String fqn) {
      try {
        // TODO 构建完全限定名
        String externalName = fqn.substring(0, fqn.indexOf('.')).replace('/', '.');
        ClassLoader loader = getClassLoader();
        if (log.isDebugEnabled()) {
          log.debug("Checking to see if class " + externalName + " matches criteria [" + test + "]");
        }
  
        // TODO 加载类
        Class<?> type = loader.loadClass(externalName);
        if (test.matches(type)) {
          matches.add((Class<T>) type);
        }
      } catch (Throwable t) {
        log.warn("Could not examine class '" + fqn + "'" + " due to a " +
            t.getClass().getName() + " with message: " + t.getMessage());
      }
    }
  ```



##### VFS：虚拟文件系统（Virtual File System）

单例模式

用于查找指定路径下的资源

- VFS抽象类：

  - 存放VFS实现类定义Class

  ```java
  /** MyBatis实现的VFS */
  public static final Class<?>[] IMPLEMENTATIONS = { JBoss6VFS.class, DefaultVFS.class };
  
  /** 用户实现的VFS added by {@link #addImplClass(Class)}. */
  public static final List<Class<? extends VFS>> USER_IMPLEMENTATIONS = new ArrayList<>();
  ```

  - Singleton Instance Holder：

  ```java
  private static class VFSHolder {
      static final VFS INSTANCE = createVFS();
  
      @SuppressWarnings("unchecked")
      static VFS createVFS() {
        // TODO 有限使用用户设置的VFS，然后使用MyBatis自带的
        List<Class<? extends VFS>> impls = new ArrayList<>();
        impls.addAll(USER_IMPLEMENTATIONS);
        impls.addAll(Arrays.asList((Class<? extends VFS>[]) IMPLEMENTATIONS));
  
        // Try each implementation class until a valid one is found
        VFS vfs = null;
        for (int i = 0; vfs == null || !vfs.isValid(); i++) {
          Class<? extends VFS> impl = impls.get(i);
          try {
            // TODO 实例化
            vfs = impl.getDeclaredConstructor().newInstance();
  					// ...
          } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
          }
        }
        return vfs;
      }
    }
  ```

  







- Resources 和 VFS 什么场景下用？？？