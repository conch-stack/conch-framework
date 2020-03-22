

binding模块：将Mapper接口与映射配置文件关联，并自动为接口生成动态代理对象



- MapperRegistry：Mapper接口的注册器
  - MapperProxyFactory：为每个Mapper接口创建一个工厂用于创建MapperProxy
    - MapperProxy：Mapper接口的动态代理对象
      - MethodInvoker：Mapper接口中的方法的自定义代理实现
        - MapperMethod：包装对应的SqlCommand (todo) 以及方法签名**（重点）**
        - MethodHandle：？？？



##### MapperRegistry：

```java
public class MapperRegistry {

  // 全局配置
  private final Configuration config; 
  // Mapper接口Class， 对应的代理构造工厂
  private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

  @SuppressWarnings("unchecked")
  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
    // 构建Mapper接口的代理对象
    return mapperProxyFactory.newInstance(sqlSession);
  }

  public <T> void addMapper(Class<T> type) {
    if (type.isInterface()) {
      boolean loadCompleted = false;
      try {
        // 创建MapperProxyFactory
        knownMappers.put(type, new MapperProxyFactory<>(type));
        // 这块设计XML和Annotation的解析逻辑，暂时不管
        MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
        // TODO 先解析注解 再解析 XMLMapper
        parser.parse();
        loadCompleted = true;
      } finally {
        if (!loadCompleted) {
          knownMappers.remove(type);
        }
      }
    }
  }

  /**
   * Adds the mappers.
   */
  public void addMappers(String packageName, Class<?> superType) {
    // 加载资源工具类
    ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
    // 在packageName下 检测是否为supperType的子类 
    resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
    Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
    for (Class<?> mapperClass : mapperSet) {
      addMapper(mapperClass);
    }
  }
}
```



##### MapperProxyFactory：

```java
public class MapperProxyFactory<T> {
  // 代理的Mapper接口Class
  private final Class<T> mapperInterface;
  // 缓存 Mapper接口中的 方法 以及方法的自定义调用反射实现（参考下面）
  private final Map<Method, MapperMethodInvoker> methodCache = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  protected T newInstance(MapperProxy<T> mapperProxy) {
    // 利用代理对象，创建T的代理，调用T会转为调用对应的MapperProxy
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }

  public T newInstance(SqlSession sqlSession) {
    // TODO 创建代理对象
    final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache);
    return newInstance(mapperProxy);
  }
}
```



##### MapperProxy：重点（ 包含 MapperMethodInvoker ）

```java
public class MapperProxy<T> implements InvocationHandler, Serializable {

  // 处理Java8 与 Java9 的兼容问题，不管
  private static final Method privateLookupInMethod;
  // TODO 关联的SqlSession
  private final SqlSession sqlSession;
  // TODO 代理的接口Class
  private final Class<T> mapperInterface;
  // TODO 缓存 Mapper接口中的 方法 以及方法的自定义调用反射实现
  private final Map<Method, MapperMethodInvoker> methodCache;

  public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethodInvoker> methodCache) {
    this.sqlSession = sqlSession;
    this.mapperInterface = mapperInterface;
    this.methodCache = methodCache;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      // TODO 如果目标方法是来自（继承）Object的方法，则直接调用
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, args);
      } else {
        // TODO 用户自定义的接口方法时
        return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
  }

  private MapperMethodInvoker cachedInvoker(Method method) throws Throwable {
    try {
      return methodCache.computeIfAbsent(method, m -> {
        // 接口的 default 方法
        if (m.isDefault()) {
          try {
            if (privateLookupInMethod == null) {
              return new DefaultMethodInvoker(getMethodHandleJava8(method));
            } else {
              return new DefaultMethodInvoker(getMethodHandleJava9(method));
            }
          } catch (IllegalAccessException | InstantiationException | InvocationTargetException
              | NoSuchMethodException e) {
            throw new RuntimeException(e);
          }
        } else {
          // 非 default 方法， 构建 PlainMethodInvoker 包含 MapperMethod
          return new PlainMethodInvoker(new MapperMethod(mapperInterface, method, sqlSession.getConfiguration()));
        }
      });
    } catch (RuntimeException re) { // ...
    }
  }

  // 构建不同JDK版本的  MethodHandle 感兴趣可以自行阅读源码
  private MethodHandle getMethodHandleJava9(Method method);
  private MethodHandle getMethodHandleJava8(Method method);

  // TODO 新增的，将 MapperMethod 又包了一层
  interface MapperMethodInvoker {
    Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable;
  }

  // TODO 正常方法用户定义的方法 包装成 MapperMethod 
  private static class PlainMethodInvoker implements MapperMethodInvoker {
    private final MapperMethod mapperMethod;

    public PlainMethodInvoker(MapperMethod mapperMethod) {
      super();
      this.mapperMethod = mapperMethod;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
      // TODO 真实调用 (参考下面)
      return mapperMethod.execute(sqlSession, args);
    }
  }
  // 接口的Default 方法 使用 MethodHandle 去绑定代理对象执行对应方法
  // TODO 研究一下 MethodHandle ？？？
  private static class DefaultMethodInvoker implements MapperMethodInvoker {
    private final MethodHandle methodHandle;

    public DefaultMethodInvoker(MethodHandle methodHandle) {
      super();
      this.methodHandle = methodHandle;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
      return methodHandle.bindTo(proxy).invokeWithArguments(args);
    }
  }
}
```





##### MapperMethod：重点（SqlCommand、MethodSignature）

```java
public class MapperMethod {
  // TODO SQL封装：包含SQL语句 + SQL执行类型（UNKNOWN, INSERT, UPDATE, DELETE, SELECT, FLUSH）
  private final SqlCommand command;
  // TODO 该方法的签名信息，包含返回类型等（参考下面）
  private final MethodSignature method;

  public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
    this.command = new SqlCommand(config, mapperInterface, method);
    this.method = new MethodSignature(config, mapperInterface, method);
  }

  // 根据不同的 SqlCommandType 执行对应逻辑（调用SqlSession接口的方法）
  public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    switch (command.getType()) {
      case INSERT: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.insert(command.getName(), param));
        break;
      }
      case UPDATE: { // ...}
      case DELETE: { // ...}
      case SELECT:
        // 依据返回值不同，调用对应的处理方法 感兴趣可以自行阅读源码
        if (method.returnsVoid() && method.hasResultHandler()) {
          executeWithResultHandler(sqlSession, args);
          result = null;
        } else if (method.returnsMany()) {
          result = executeForMany(sqlSession, args);  // 处理数组或Collection集合返回值
        } else if (method.returnsMap()) {
          result = executeForMap(sqlSession, args);
        } else if (method.returnsCursor()) {
          result = executeForCursor(sqlSession, args);
        } else {
          Object param = method.convertArgsToSqlCommandParam(args);
          result = sqlSession.selectOne(command.getName(), param);
          if (method.returnsOptional()
              && (result == null || !method.getReturnType().equals(result.getClass()))) {
            result = Optional.ofNullable(result);
          }
        }
        break;
      case FLUSH:
        result = sqlSession.flushStatements();
        break;
    }
    return result;
  }
  
  public static class SqlCommand {

    // TODO SQL 语句的ID   接口名+方法名
    private final String name;
    // TODO SQL 的执行类型
    private final SqlCommandType type;

    public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
      final String methodName = method.getName();
      final Class<?> declaringClass = method.getDeclaringClass();
      // TODO 从config中获取对应  MappedStatement（解析好的sql）
      MappedStatement ms = resolveMappedStatement(mapperInterface, methodName, declaringClass,
          configuration);
      if (ms == null) {
        if (method.getAnnotation(Flush.class) != null) {
          name = null;
          type = SqlCommandType.FLUSH;
        } else {
          throw new BindingException("Invalid bound statement (not found): "
              + mapperInterface.getName() + "." + methodName);
        }
      } else {
        name = ms.getId();  // 给name赋值
        type = ms.getSqlCommandType();
        if (type == SqlCommandType.UNKNOWN) {
          throw new BindingException("Unknown execution method for: " + name);
        }
      }
    }

    private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName,
        Class<?> declaringClass, Configuration configuration) {
      // TODO statementId 接口全类名+方法名
      String statementId = mapperInterface.getName() + "." + methodName;
      if (configuration.hasStatement(statementId)) {
        return configuration.getMappedStatement(statementId);
      } else if (mapperInterface.equals(declaringClass)) {
        return null;
      }
      for (Class<?> superInterface : mapperInterface.getInterfaces()) {
        if (declaringClass.isAssignableFrom(superInterface)) {
          MappedStatement ms = resolveMappedStatement(superInterface, methodName,
              declaringClass, configuration);
          if (ms != null) {
            return ms;
          }
        }
      }
      return null;
    }
  }

  // 方法的签名信息
  public static class MethodSignature {

    private final boolean returnsMany; // 返回类型是否为Collection类型或者数组
    private final boolean returnsMap; // 返回类型是否为Map类型
    private final boolean returnsVoid; // 返回类型是否为void
    private final boolean returnsCursor; // 返回类型是否为Cursor（游标）
    private final boolean returnsOptional; // 返回类型是Optional类型
    private final Class<?> returnType; // 返回值类型
    private final String mapKey;  // 如果返回值是Map，则记录其作为key的列名
    // 用于标识该方法参数列表中ResultHandler类型参数的位置（因为它不会被记录进参数names中）
    private final Integer resultHandlerIndex;
    // 用于标识该方法参数列表中RowBounds类型参数的位置（因为它不会被记录进参数names中）
    private final Integer rowBoundsIndex;
    // TODO 处理Mapper方法的参数列表 （具体参考下面）
    private final ParamNameResolver paramNameResolver;

    public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
      // TODO 解析返回值类型
      Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
      if (resolvedReturnType instanceof Class<?>) {
        this.returnType = (Class<?>) resolvedReturnType;
      } else if (resolvedReturnType instanceof ParameterizedType) {
        this.returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
      } else {
        this.returnType = method.getReturnType();
      }
      // TODO 判断 返回值类型
      this.returnsVoid = void.class.equals(this.returnType);
      this.returnsMany = configuration.getObjectFactory().isCollection(this.returnType) || this.returnType.isArray();
      this.returnsCursor = Cursor.class.equals(this.returnType);
      this.returnsOptional = Optional.class.equals(this.returnType);
      this.mapKey = getMapKey(method);
      this.returnsMap = this.mapKey != null;
      this.rowBoundsIndex = getUniqueParamIndex(method, RowBounds.class);
      this.resultHandlerIndex = getUniqueParamIndex(method, ResultHandler.class);
      // 参数名处理
      this.paramNameResolver = new ParamNameResolver(configuration, method);
    }
    // ...
    
    // TODO 重要 获取 参数名 与 实参的对应关系
    public Object convertArgsToSqlCommandParam(Object[] args) {
      return paramNameResolver.getNamedParams(args);
    }
  }
}
```



##### ParamNameResolver：处理Mapper接口中的 参数列表

```java
public class ParamNameResolver {

  public static final String GENERIC_NAME_PREFIX = "param";

  /**
   * 记录参数在参数列表中的位置索引及参数名称之间的对应关系
   * key: 索引
   * value: 参数名  可以通过 @Param指定，没有指定则使用 参数索引作为其名称；
   *        如果参数列表中包含RowBounds类型或者ResultHandler类型，则这两种类型的
   *        参数并不会记录进name集合，这样会导致参数的索引和名称不一致
   */
  private final SortedMap<Integer, String> names;
  // TODO 记录是否使用了 @Param注解
  private boolean hasParamAnnotation;

  // TODO 反射获取信息
  public ParamNameResolver(Configuration config, Method method) {
    // 获取每个参数的类型
    final Class<?>[] paramTypes = method.getParameterTypes();
    // 获取参数列表上的注解
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    final SortedMap<Integer, String> map = new TreeMap<>();
    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
      if (isSpecialParameter(paramTypes[paramIndex])) {
        // skip special parameters
        continue;
      }
      String name = null;
      // 解析 @Param注解
      for (Annotation annotation : paramAnnotations[paramIndex]) {
        if (annotation instanceof Param) {
          hasParamAnnotation = true;
          name = ((Param) annotation).value();
          break;
        }
      }
      if (name == null) {
        // @Param was not specified.
        // 如果这个参数没有 @Param注解，转为使用反射拿取其索引位置处的方法名
        if (config.isUseActualParamName()) {
          name = getActualParamName(method, paramIndex);
        }
        if (name == null) {
          // use the parameter index as the name ("0", "1", ...)
          // gcode issue #71
          // 如果还没解析到，那么就使用 index 为其 name
          name = String.valueOf(map.size());
        }
      }
      map.put(paramIndex, name);
    }
    names = Collections.unmodifiableSortedMap(map);
  }

  // 获取对应索引位置的方法名
  private String getActualParamName(Method method, int paramIndex) {
    return ParamNameUtil.getParamNames(method).get(paramIndex);
  }

  /**
   * TODO 将实参与对应名称关联
   */
	public Object getNamedParams(Object[] args) {
    final int paramCount = names.size();
    if (args == null || paramCount == 0) { // 无参，返回null
      return null;
    } else if (!hasParamAnnotation && paramCount == 1) { // 未使用@Param注解，且只有一个参数
      return args[names.firstKey()];
    } else {  // 处理 @Param 注解
      // key: 参数名
      // value: 对应实参
      final Map<String, Object> param = new ParamMap<>();
      int i = 0;
      for (Map.Entry<Integer, String> entry : names.entrySet()) {
        param.put(entry.getValue(), args[entry.getKey()]);
        // add generic param names (param1, param2, ...)
        final String genericParamName = GENERIC_NAME_PREFIX + (i + 1);
        // ensure not to overwrite parameter named with @Param
        // TODO 保证未使用@Param注解的参数解析， 如果对应 generic param name 已经存在 names 集合中，则忽略
        if (!names.containsValue(genericParamName)) {
          param.put(genericParamName, args[entry.getKey()]);
        }
        i++;
      }
      return param;
    }
  }
}
```



##### MethodHandle：需要研究一下？？？