## Spring AOP源码分析（一）

##### 要素：

- target：被代理类
- advisor:advice+pointcut ：切面（增强器）
- AutoProxyCreator：用于创建代理对象

##### 关键点：

- Spring AOP也是对目标类增强，生成代理类。但是与AspectJ的最大区别在于---Spring AOP的运行时增强，而AspectJ是编译时增强。
- 一个@AspectJ注解对应多个Advisor，一个Advisor对应一个Advice+其适配的一个Pointcut
- 问题：@Pointcut是在哪里解析的，和 Pointcut 这个类有什么关系？？？

![image-20200528233827147](assets/image-20200528233827147.png)

##### 源码：

- AopNamespaceHandler：注册BeanDefinitionParser来处理Aop的xml标签或者自动代理的internalAutoProxyCreator（可以自动创建代理对象）

- AopNamespaceUtils：#registerAspectJAnnotationAutoProxyCreatorIfNecessary
  
  > NamespaceHandler是Spring定义的解析XML的namespace处理器（也就是xml文件头那一串schema）
  
  - AopConfigUtils#registerAspectJAnnotationAutoProxyCreatorIfNecessary 注册 internalAutoProxyCreator的BeanDefinition到BeanDefinitionRegistry
    
    - internalAutoProxyCreator：三种
      - InfrastructureAdvisorAutoProxyCreator
    - AspectJAwareAdvisorAutoProxyCreator
      - AnnotationAwareAspectJAutoProxyCreator 优先级最高
  
  - \#useClassProxyingIfNecessary：
    
    - proxyTargetClass：true表示强制使用CGLIB代理
    
    - JDK代理目标类必须实现某个接口：运行时创建该接口的实现类来实现代理逻辑
  
  - CGLIB通过创建目标类子类来实现代理逻辑，基于ASM字节码编辑类库，性能好
    
    - exposeProxy：**有时候目标对象内部的自我调用将无法实现切面中的增强**
      
      - ```java
        public interface Aservice {
          void a();
          void b();
        }
        
        public class AserviceImpl implements Aservice {
          @Transactional
            public void a() { this.b() }
        
            @Transactional
            public void b() {}
        }
        ```
        
        ```java
        
        ```
  
  如上代码，默认情况下，a() 调 b() 时，b方法的事务会失效。
  
    如果exposeProxy=true，然后将以上代码中的 this.b() 修改为如下，则事务不会失效
  
  ```java
  public class AserviceImpl implements Aservice {
        @Transactional
        public void a() { (AService)AopContext.currentProxy().b() }
  
        @Transactional
        public void b() {}
  }
  ```
  
  
  
  - AnnotationAwareAspectJAutoProxyCreator：
    - 核心代理逻辑在这个类里面的父类**AbstractAutoProxyCreator**中
    - Step1：通过实现SpringBean生命周期中的SmartInstantiationAwareBeanPostProcessor的**#postProcessBeforeInstantiation**方法
      - **ProxyProcessorSupport 中设置了 该 BeanPostProcessor的优先级最低**，这很重要，不会影响别的BeanPostProcessor的执行，因为Spring在调用所有BeanPostProcessor的地方，发现如果postProcessBeforeInstantiation方法返回了非null对象，则跳出循环
      - createProxy来打乱Bean实例化 | **前提：我们有一个 通用的 custom TargetSource ？？？**
      - shouldSkip(beanClass, beanName)：会预先实例化一波Advisor，并标记跳过不需要执行代理增强的类，在后续初始化后回调可以跳过
    - Step2：通过实现SpringBean生命周期中的SmartInstantiationAwareBeanPostProcessor的**#postProcessAfterInitialization**方法
      - 在Spring创建Bean时AbstractAutowireCapableBeanFactory#doCreateBean会默认为允许**循环引用**的每个单例Bean创建一个由CGLIB代理的引用，会放入每个SmartInstantiationAwareBeanPostProcessor的earlyProxyReferences缓存中
      - postProcessAfterInitialization初始化后，执行时会从当前earlyProxyReferences获取并判断是否需要进行代理createProxy
      - wrapIfNecessary#getAdvicesAndAdvisorsForBean：// Create proxy if we have advice. 表示这个方法会扫描所有advice
      - getAdvicesAndAdvisorsForBean#findEligibleAdvisors
  
  ```java
  // AbstractAdvisorAutoProxyCreator.class
  protected Object[] getAdvicesAndAdvisorsForBean(
        Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {
       // 参考下面代码
     List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
     if (advisors.isEmpty()) {
        return DO_NOT_PROXY;   // 表示系统未配置任何advisor（advisor:advice+pointcut ：切面（增强器））
     }
     return advisors.toArray();
  }
  ```
  
  ```java
  protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
    // 先找到所有Advisor 有缓存 参考下面
    List<Advisor> candidateAdvisors = findCandidateAdvisors();
    // 从所有Advisor中查找匹配当前beanClass的切面增强 参考下面
    List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
    // 扩展 判断是否需要组合 Advisor Chain
    // AspectJProxyUtils.makeAdvisorChainAspectJCapableIfNecessary(candidateAdvisors);
    extendAdvisors(eligibleAdvisors);
    if (!eligibleAdvisors.isEmpty()) {
      eligibleAdvisors = sortAdvisors(eligibleAdvisors);
    }
    return eligibleAdvisors;
  }
  ```
  
  - AnnotationAwareAspectJAutoProxyCreator.class # findCandidateAdvisors
  
  ```java
  @Override
  protected List<Advisor> findCandidateAdvisors() {
    // Add all the Spring advisors found according to superclass rules.
      // 在BeanFactory中查找所有符合Spring的 Advisor.class 类型的BeanName，并调用getBean进行实例化成Advisor
    // String[] advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.beanFactory, Advisor.class, true, false);
    List<Advisor> advisors = super.findCandidateAdvisors();
    // Build Advisors for all AspectJ aspects in the bean factory.
    // 查找所有 AspectJ 定义的 Advisor 
    // 通过 aspectJAdvisorsBuilder.buildAspectJAdvisors() 这个Builder去构建
    // String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.beanFactory, Object.class, true, false);
    // 在BeanFactory中查找所有 Object.class ，获取所有BeanDefinition的beanNames，获取其beanType，判断是否为 isAspect 
    // 最后调用工厂去构建 advisorFactory.getAdvisors(factory) 具体流程参考后面：构建流程
    if (this.aspectJAdvisorsBuilder != null) {
      advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
    }
    return advisors;
  }
  ```
  
  - findAdvisorsThatCanApply#AopUtils.findAdvisorsThatCanApply#canApply
  
  ```java
  public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
    // 分为基础设施的Advisor
    if (advisor instanceof IntroductionAdvisor) {
      // 使用类过滤器进行匹配当前目标类
      return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
    }
    // 和基于Pointcut的Advisor
    else if (advisor instanceof PointcutAdvisor) {
      PointcutAdvisor pca = (PointcutAdvisor) advisor;
      return canApply(pca.getPointcut(), targetClass, hasIntroductions);
    }
    else {
      // It doesn't have a pointcut so we assume it applies.
      return true;
    }
  }
  ```
  - AspectJExpressionPointcut：Pointcut封装，包含
  
    ```java
     /**
      * 类过滤器
      */
      ClassFilter getClassFilter();
    
      /**
      * 方法过滤器
      */
      MethodMatcher getMethodMatcher();
    
      /**
      * 增强表达式
      */
      @Nullable
      String getExpression();
    ```
  
  - 说明：已经有类过滤器了，为什么还需要方法过滤器？
    - 答：可复用相同方法匹配的场景
  - 如何处理类过滤器+方法过滤器
    - 创建过滤器：AspectJExpressionPointcut
  
    ```java
    @Override
    public ClassFilter getClassFilter() {  // 获取时，会去创建 PointcutExpression ；基于 PointcutParser
    	obtainPointcutExpression();
    	return this;
    }
    
    @Override
    public MethodMatcher getMethodMatcher() {
    	obtainPointcutExpression();
    	return this;
    }
    
    // 获得 PointcutExpression
    private PointcutExpression obtainPointcutExpression() {
        if (getExpression() == null) {
          throw new IllegalStateException("Must set property 'expression' before attempting to match");
        }
        if (this.pointcutExpression == null) {
          this.pointcutClassLoader = determinePointcutClassLoader();
          this.pointcutExpression = buildPointcutExpression(this.pointcutClassLoader);
        }
        return this.pointcutExpression;
    }
    
    // 构建 PointcutExpression
    private PointcutExpression buildPointcutExpression(@Nullable ClassLoader classLoader) {
        // 初始化 PointcutParser  核心的解析表达式逻辑就在这里了，包含注解的表达式解析
        PointcutParser parser = initializePointcutParser(classLoader);
        PointcutParameter[] pointcutParameters = new PointcutParameter[this.pointcutParameterNames.length];
        for (int i = 0; i < pointcutParameters.length; i++) {
          pointcutParameters[i] = parser.createPointcutParameter(
            this.pointcutParameterNames[i], this.pointcutParameterTypes[i]);
        }
        return parser.parsePointcutExpression(replaceBooleanOperators(resolveExpression()),
                                              this.pointcutDeclarationScope, pointcutParameters);
    }
    ```
  
    - wrapIfNecessary#createProxy
  
    ```java
    // specificInterceptors 就是 所有匹配的 Advisor
    Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
    Object proxy = createProxy(
                        bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
    ```
  
    - AbstractAutoProxyCreator#createProxy
  
    ```java
    // AopProxyFactory#DefaultAopProxyFactory
    public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
      if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
        Class<?> targetClass = config.getTargetClass();
        if (targetClass == null) {
          throw new AopConfigException("TargetSource cannot determine target class: " +
                                       "Either an interface or a target is required for proxy creation.");
        }
        if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
          return new JdkDynamicAopProxy(config);   // jdk动态代理 关注其 invoke 方法
        }
        return new ObjenesisCglibAopProxy(config);  // cglib动态代理  DynamicAdvisedInterceptor#intercept 方法
      }
      else {
        return new JdkDynamicAopProxy(config);
      }
    }
    
    // ObjenesisCglibAopProxy super CglibAopProxy # getProxy() # getCallbacks() 获取所有回调
    Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);
    // class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable
    // 最终的调用点
    Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
    
    ```
  
    - Aop多重代理（代理链 - 责任链）：原理可参考[使用 Cglib 实现多重代理](https://www.jianshu.com/p/9ba77d8f200b)
  
    ```java
    // Get the interception chain for this method.
    List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
    
    // AdvisorChainFactory | DefaultAdvisorChainFactory
    
    // Get the interception chain for this method.
    // 3.获取拦截器链：例如使用@Around注解时会找到AspectJAroundAdvice，还有ExposeInvocationInterceptor
    List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
    
    // 4.检查我们是否有任何拦截器（advice）。 如果没有，直接反射调用目标，并避免创建MethodInvocation。
    if (chain.isEmpty()) {
      Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
      // 5.不存在拦截器链，则直接进行反射调用
      retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
    } else {
      // We need to create a method invocation...
      // 6.如果存在拦截器，则创建一个ReflectiveMethodInvocation：代理对象、被代理对象、方法、参数、
      // 被代理对象的Class、拦截器链作为参数创建ReflectiveMethodInvocation
      invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
      // Proceed to the joinpoint through the interceptor chain.
      // 7.触发ReflectiveMethodInvocation的执行方法
      retVal = invocation.proceed();
    }
    ```
  
    - 自调用暴露代理原理
  
    ```java
    // 有时候目标对象内部的自我调用将无法实施切面中的增强则需要通过此属性暴露代理
    if (this.advised.exposeProxy) {
            // Make invocation available if necessary.
          // 将当前的代理对象暴露到 ThreadLocal中，使得方法内自调用时可以使用
          // 最好的方式是不要出现自调用逻辑
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
    }
    ```
  
- ScopedProxyFactoryBean#AopInfrastructureBean

- 构建流程
  
  - BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors()：构建
  
  - 从BeanFactroy中获取所有的beanNames：
    
    ```java
    String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                                this.beanFactory, Object.class, true, false);
    
    // 用工厂进行 Advisor 的创建+获取
    MetadataAwareAspectInstanceFactory factory =
                                            new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);
    List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);
    ```
  
  - ReflectiveAspectJAdvisorFactory核心Advisor解析类：即AspectJAdvisorFactory是 this.advisorFactory的具体实现类
  
  - 迭代获取所有 非 @PointCut 注解的方法：getAdvisorMethods
  
  ```java
  // ReflectiveAspectJAdvisorFactory#getAdvisors()
  for (Method method : getAdvisorMethods(aspectClass)) { // 参考下面
    // 解析包装成 Advisor 参考下面
    Advisor advisor = getAdvisor(method, lazySingletonAspectInstanceFactory, advisors.size(), aspectName);
    if (advisor != null) {
      advisors.add(advisor);
    }
  }
  
  // ReflectiveAspectJAdvisorFactory#getAdvisors()#getAdvisorMethods()
  private List<Method> getAdvisorMethods(Class<?> aspectClass) {
    final List<Method> methods = new ArrayList<>();
    // 反射获取所有 非 @PointCut 注解 标注的方法
    ReflectionUtils.doWithMethods(aspectClass, method -> {
      // Exclude pointcuts
      if (AnnotationUtils.getAnnotation(method, Pointcut.class) == null) {
        methods.add(method);
      }
    }, ReflectionUtils.USER_DECLARED_METHODS);
    if (methods.size() > 1) {
      methods.sort(METHOD_COMPARATOR);
    }
    return methods;
  }
  
  // ReflectiveAspectJAdvisorFactory#getAdvisor()
  @Override
  @Nullable
  public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
                            int declarationOrderInAspect, String aspectName) {
  
    validate(aspectInstanceFactory.getAspectMetadata().getAspectClass());
  
    // 注解信息包装成：AspectJExpressionPointcut 参考下面：
    AspectJExpressionPointcut expressionPointcut = getPointcut(
      candidateAdviceMethod, aspectInstanceFactory.getAspectMetadata().getAspectClass());
    if (expressionPointcut == null) {
      return null;
    }
    // 将Method对象和AspectJExpressionPointcut封装到 InstantiationModelAwarePointcutAdvisorImpl中成为一个 Advisor （增强器）
    // 会调用instantiateAdvice进而调用下面的getAdvice构建
    // 在AspectJ中实质都是一个 PointcutAdvisor.class
    return new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod,
                                                          this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
  }
  
  // 具体解析工作
  @Nullable
  private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass) {
    // 
    AspectJAnnotation<?> aspectJAnnotation =
      AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
    if (aspectJAnnotation == null) {
      return null;
    }
  
    AspectJExpressionPointcut ajexp =
      new AspectJExpressionPointcut(candidateAspectClass, new String[0], new Class<?>[0]);
    ajexp.setExpression(aspectJAnnotation.getPointcutExpression());
    if (this.beanFactory != null) {
      ajexp.setBeanFactory(this.beanFactory);
    }
    return ajexp;
  }
  
  // 构建 Advice
  @Override
  @Nullable
  public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
                          MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {
  
    Class<?> candidateAspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
    validate(candidateAspectClass);
  
    AspectJAnnotation<?> aspectJAnnotation =
      AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
    if (aspectJAnnotation == null) {
      return null;
    }
  
    // If we get here, we know we have an AspectJ method.
    // Check that it's an AspectJ-annotated class
    if (!isAspect(candidateAspectClass)) {
      throw new AopConfigException("Advice must be declared inside an aspect type: " +
                                   "Offending method '" + candidateAdviceMethod + "' in class [" +
                                   candidateAspectClass.getName() + "]");
    }
  
    if (logger.isDebugEnabled()) {
      logger.debug("Found AspectJ method: " + candidateAdviceMethod);
    }
  
    AbstractAspectJAdvice springAdvice;
  
    switch (aspectJAnnotation.getAnnotationType()) {
      case AtPointcut: // 跳过
        if (logger.isDebugEnabled()) {
          logger.debug("Processing pointcut '" + candidateAdviceMethod.getName() + "'");
        }
        return null;
      case AtAround:
        springAdvice = new AspectJAroundAdvice(
          candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
        break;
      case AtBefore:
        springAdvice = new AspectJMethodBeforeAdvice(
          candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
        break;
      case AtAfter:
        springAdvice = new AspectJAfterAdvice(
          candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
        break;
      case AtAfterReturning:
        springAdvice = new AspectJAfterReturningAdvice(
          candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
        AfterReturning afterReturningAnnotation = (AfterReturning) aspectJAnnotation.getAnnotation();
        if (StringUtils.hasText(afterReturningAnnotation.returning())) {
          springAdvice.setReturningName(afterReturningAnnotation.returning());
        }
        break;
      case AtAfterThrowing:
        springAdvice = new AspectJAfterThrowingAdvice(
          candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
        AfterThrowing afterThrowingAnnotation = (AfterThrowing) aspectJAnnotation.getAnnotation();
        if (StringUtils.hasText(afterThrowingAnnotation.throwing())) {
          springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
        }
        break;
      default:
        throw new UnsupportedOperationException(
          "Unsupported advice type on method: " + candidateAdviceMethod);
    }
  
    // Now to configure the advice...
    springAdvice.setAspectName(aspectName);
    springAdvice.setDeclarationOrder(declarationOrder);
    String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
    if (argNames != null) {
      springAdvice.setArgumentNamesFromStringArray(argNames);
    }
    springAdvice.calculateArgumentBindings();
  
    return springAdvice;
  
  }
  ```
  



- AspectJExpressionPointcut会解析注解中的表达式，并以此匹配类型和方法以绝对是否进行拦截并增强 
  - 问题：什么时候触发解析？？？
  - 答：第一调用 findAdvisorsThatCanApply 时
- AOP CGLib代理源码分析(**https://blog.csdn.net/v123411739/article/details/106065775**)
-  Other
  - 比较高阶

```java
private static final Comparator<Method> METHOD_COMPARATOR;

    static {
        Comparator<Method> adviceKindComparator = new ConvertingComparator<>(
                new InstanceComparator<>(
                        Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class),
                (Converter<Method, Annotation>) method -> {
                    AspectJAnnotation<?> annotation =
                        AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(method);
                    return (annotation != null ? annotation.getAnnotation() : null);
                });
        Comparator<Method> methodNameComparator = new ConvertingComparator<>(Method::getName);
        METHOD_COMPARATOR = adviceKindComparator.thenComparing(methodNameComparator);
    }
```

- SpringBoot  | @Import 注解的执行逻辑，它也是Spring提供的，应该是和@Configuration在同一生命周期中
  
  ```java
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Import(AspectJAutoProxyRegistrar.class)
  public @interface EnableAspectJAutoProxy {
  
    /**
     * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
     * to standard Java interface-based proxies. The default is {@code false}.
     */
    boolean proxyTargetClass() default false;
  
    /**
     * Indicate that the proxy should be exposed by the AOP framework as a {@code ThreadLocal}
     * for retrieval via the {@link org.springframework.aop.framework.AopContext} class.
     * Off by default, i.e. no guarantees that {@code AopContext} access will work.
     * @since 4.3.1
     */
    boolean exposeProxy() default false;
  
  }
  ```

- 优秀文章
  
  - https://joonwhee.blog.csdn.net/article/details/106065748
  - https://blog.csdn.net/qq_20597727/article/details/84800176

- 代码流程：
  
  ```
  AnnotationAwareAspectJAutoProxyCreator
  AbstractAutoProxyCreator#postProcessAfterInitialization
  代码块1：wrapIfNecessary
  代码块2：getAdvicesAndAdvisorsForBean
  代码块3：findEligibleAdvisors
  代码块4：findAdvisorBeans
  代码块5：findCandidateAdvisors
  代码块6：buildAspectJAdvisors
  代码块7：getAdvisors
  代码块8：getAdvisor
  代码块9：getPointcut
  代码块10：findAspectJAnnotationOnMethod
  代码块11：new InstantiationModelAwarePointcutAdvisorImpl
  代码块12：instantiateAdvice
  代码块13：findAdvisorsThatCanApply
  代码块14：createProxy
  代码块15：getProxy
  代码块16：createAopProxy
  代码块17：JDK 动态代理、CBLIB 代理构造函数
  代码块18：JdkDynamicAopProxy#getProxy
  代码块19：CglibAopProxy#getProxy
  代码块20：getCallbacks
  ```