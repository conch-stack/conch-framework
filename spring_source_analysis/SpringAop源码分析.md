## Spring AOP源码分析



##### 要素：

- target：被代理类
- advisor:advice+pointcut ：切面
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
  
  - ScopedProxyFactoryBean#AopInfrastructureBean



- 构建流程

  - BeanDefinition解析后，添加Aop的BeanPostProcessor：AnnotationAwareAspectJAutoProxyCreator

  - 后置解析器，实例化Bean后，赋值后(populateBean)

    ```java
    protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
    		if (System.getSecurityManager() != null) {
    			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            // Aware 方法执行 BeanNameAware | BeanClassLoaderAware | BeanFactoryAware
    				invokeAwareMethods(beanName, bean);
    				return null;
    			}, getAccessControlContext());
    		}
    		else {
    			invokeAwareMethods(beanName, bean);
    		}
    
    		Object wrappedBean = bean;
    		if (mbd == null || !mbd.isSynthetic()) {
          // 处理
    			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    		}
    
    		try {
    			invokeInitMethods(beanName, wrappedBean, mbd);
    		}
    		catch (Throwable ex) {
    			throw new BeanCreationException(
    					(mbd != null ? mbd.getResourceDescription() : null),
    					beanName, "Invocation of init method failed", ex);
    		}
    		if (mbd == null || !mbd.isSynthetic()) {
    			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    		}
    
    		return wrappedBean;
    	}
    ```

    

