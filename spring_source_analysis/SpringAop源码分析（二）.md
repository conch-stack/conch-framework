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

