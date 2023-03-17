## SpringAop源码分析（二）

##### 问题：为什么Spring不需要实现 Around Advice

线索：

- AspectJ@Around与org.aspectj..lang.ProceedingJoinPoint配合执行被代理方法
- ProceedingJoinPointi#proceed()方法类似于Java Method:#invoke(Object.,Object)
- Spring AOP底层API ProxyFactory可通过addAdvice方法与Advice实现关联
- 接口Advice是Interceptor的父亲接口，而接口Methodlnterceptor又扩展了Interceptor
- MethodInterceptor的invoke方法参数MethodInvocation与ProceedingJoinPoint类似



##### 控制 Advice执行顺序

- 可使用 Ordered 控制 Advice的执行顺序



##### 自动动态代理

- org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator
- org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
- **org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator**



##### TargetSource

Spring用于跟踪保持目标源的类

- org.springframework.aop.target.HotSwappableTargetSource
- org.springframework.aop.target.AbstractPoolingTargetSource
- org.springframework.aop.target.PrototypeTargetSource
- org.springframework.aop.target.ThreadLocalTargetSource
- org.springframework.aop.target.SingletonTargetSource
- org.springframework.aop.target.LazyInitTargetSource



##### Joinpoint

Interceptor 执行上下文： Invocation

- MethodInvocation : 方法级别
- *ConstructorInvocation：构造器级别* - Spring未支持，AspectJ支持

MethodInvocation实现

- ProxyMethodInvocation -> ReflectiveMethodInvocation
- ProxyMethodInvocation -> ReflectiveMethodInvocation -> CglibMethodInvocation

