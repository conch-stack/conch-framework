## Spring AOP源码分析



##### 要素：

- target：被代理类
- advisor:advice+pointcut ：切面（增强器）
- AutoProxyCreator：用于创建代理对象



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
  
    - 方式一：通过实现SpringBean生命周期中的SmartInstantiationAwareBeanPostProcessor的#postProcessBeforeInstantiation方法#createProxy来打乱Bean实例化
      
      - **前提：我们有一个 通用的 custom TargetSource ？？？**
      
    - 方式二：通过实现SpringBean生命周期中的SmartInstantiationAwareBeanPostProcessor的#postProcessAfterInitialization方法
  
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
      
      
      protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        // 先找到所有Advisor 有缓存 参考下面
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        // 从所有Advisor中查找匹配当前beanClass的切面增强 参考下面
        List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
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
          return new JdkDynamicAopProxy(config);   // jdk动态代理 关注去 invoke 方法
        }
        return new ObjenesisCglibAopProxy(config);  // cglib动态代理
      }
      else {
        return new JdkDynamicAopProxy(config);
      }
    }
    ```
  
    - Aop多重代理（代理链）：原理可参考[使用 Cglib 实现多重代理](https://www.jianshu.com/p/9ba77d8f200b)
  
    ```java
    // Get the interception chain for this method.
    List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
    
    // AdvisorChainFactory | DefaultAdvisorChainFactory
    ```
  
    
  
  
  
  
  
  - ScopedProxyFactoryBean#AopInfrastructureBean



- 构建流程

  - 从BeanFactroy中获取所有的beanNames：

    ```java
    String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
    							this.beanFactory, Object.class, true, false);
    ```

  - 迭代获取所有 非 @PointCut 注解的方法：getAdvisorMethods

  - 注解信息包装成：AspectJExpressionPointcut

  - 将Method对象和AspectJExpressionPointcut封装到 InstantiationModelAwarePointcutAdvisorImpl中成为一个 Advisor （增强器）| 实质都是一个 PointcutAdvisor.class

    ```java
    List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);
    ```

  - AspectJExpressionPointcut会解析注解中的表达式，并以此匹配类型和方法以绝对是否进行拦截并增强



- Other

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

  