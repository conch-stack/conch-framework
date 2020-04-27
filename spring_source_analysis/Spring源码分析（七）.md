## Spring源码分析（七）

> Scope SpringBean作用域



- Spring Bean作用域
  - @Scope
    - ConfigurableBeanFactory.SCOPE_SINGLETON
    - ConfigurableBeanFactory.SCOPE_PROTOTYPE
  - @RequestScope
    - WebApplicationContext.SCOPE_REQUEST
    - WebApplicationContext.SCOPE_SESSION
    - WebApplicationContext.SCOPE_APPLICATION



- singleton 作用域
  - DeanDefinition中定义
- prototype 作用域
  - 无论是依赖注入还是依赖查找，都会生成新的Bean对象！！！
    - 主要依赖注入时是新生成对象，没有问题，但是依赖查找时也生成新的对象，这就有点尴尬了
  - 依赖查找时，原型对象和单例对象都只有一份！！！
    - 单例对象只有一份没有问题，原型对象也只有一份（而且是新生成的），这也比较尴尬了
  - 所有：无论是注入还是查找，prototype的对象都是新生成的
  - Spring容器无法管理 prototype 类型的Bean的完整生命周期，也没有办法记录实例的存在，销毁方法也无法执行，与spring容器脱钩；



- request 作用域
  
  - @RequestScope 
  - 前端渲染时，会新生成对象，类似原型对象，但是后端生成的对象时CGLib代理的对象，是不变的
  
- session 作用域

  - @SessionScope
  - 浏览器待cookie请求后端时，一个cookie只会对应一个对象，同一cookie多次清除，不会生成新对象
  - 会有同步操作：多个request请求来时，作用域不共享，为保证并发session一致，会进行同步块加锁

  ```java
  public class SessionScope extends AbstractRequestAttributesScope {
  
  	@Override
  	protected int getScope() {
  		return RequestAttributes.SCOPE_SESSION;
  	}
  
  	@Override
  	public String getConversationId() {
  		return RequestContextHolder.currentRequestAttributes().getSessionId();
  	}
  
  	@Override
  	public Object get(String name, ObjectFactory<?> objectFactory) {
  		Object mutex = RequestContextHolder.currentRequestAttributes().getSessionMutex();
  		synchronized (mutex) {
  			return super.get(name, objectFactory);
  		}
  	}
  
  	@Override
  	@Nullable
  	public Object remove(String name) {
  		Object mutex = RequestContextHolder.currentRequestAttributes().getSessionMutex();
  		synchronized (mutex) {
  			return super.remove(name);
  		}
  	}
  
  }
  ```

  

- application 作用域
  - @ApplicationScope
  - API: ServletContextScope，可debug其get方法



- websocket 作用域



- 自定义 作用域





- SpringCloud中的作用域

