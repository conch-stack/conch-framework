## Tomcat源码分析（三）



### Executor

Tomcat定制版线程池

```java
// 默认情况下，Tomcat的所有工作线程都是守护线程
protected boolean daemon = true;

/**
 * max number of threads
 */
protected int maxThreads = 200;

/**
 * min number of threads
 */
protected int minSpareThreads = 25;

/**
 * idle time in milliseconds
 */
protected int maxIdleTime = 60000;

/**
 * The maximum number of elements that can queue up before we reject them
 */
protected int maxQueueSize = Integer.MAX_VALUE;
```

```java


protected void startInternal() throws LifecycleException {
    //定制版的任务队列
    taskqueue = new TaskQueue(maxQueueSize);
    //定制版的线程工厂
    TaskThreadFactory tf = new TaskThreadFactory(namePrefix,daemon,getThreadPriority());
    //定制版的线程池
    executor = new ThreadPoolExecutor(getMinSpareThreads(), getMaxThreads(), maxIdleTime, TimeUnit.MILLISECONDS, taskqueue, tf);
    executor.setThreadRenewalDelay(threadRenewalDelay);
    if (prestartminSpareThreads) {
        executor.prestartAllCoreThreads();
    }
    taskqueue.setParent(executor);

    setState(LifecycleState.STARTING);
}
```



**定制核心：Tomcat定制了任务处理流程**

Java原生线程池任务处理逻辑：

1. 前corePoolSize个任务时，来一个任务就创建一个新线程。
2. 后面再来任务，就把任务添加到任务队列里让所有的线程去抢，如果队列满了就创建临时线程。
3. 如果总线程数达到maximumPoolSize，**执行拒绝策略。**

Tomcat线程池任务处理逻辑：

1. 前corePoolSize个任务时，来一个任务就创建一个新线程。
2. 再来任务的话，就把任务添加到任务队列里让所有的线程去抢，如果队列满了就创建临时线程。
3. 如果总线程数达到maximumPoolSize，**则继续尝试把任务添加到任务队列中去。**
4. **如果缓冲队列也满了，插入失败，执行拒绝策略。**

Tomcat的maxQueueSize默认是无限制的，为了能够触发新线程的创建，Tomcat维护了**已提交任务数**这个变量。
只有当前线程数大于核心线程数、小于最大线程数，并且已提交的任务个数大于当前线程数时，也就是说线程不够用了，但是线程数又没达到极限，才会去创建新的线程

```java
public class TaskQueue extends LinkedBlockingQueue<Runnable> {

  ...
   @Override
  //线程池调用任务队列的方法时，当前线程数肯定已经大于核心线程数了
  public boolean offer(Runnable o) {

      //如果线程数已经到了最大值，不能创建新线程了，只能把任务添加到任务队列。
      if (parent.getPoolSize() == parent.getMaximumPoolSize()) 
          return super.offer(o);
          
      //执行到这里，表明当前线程数大于核心线程数，并且小于最大线程数。
      //表明是可以创建新线程的，那到底要不要创建呢？分两种情况：
      
      //1. 如果已提交的任务数小于当前线程数，表示还有空闲线程，无需创建新线程
      if (parent.getSubmittedCount()<=(parent.getPoolSize())) 
          return super.offer(o);
          
      //2. 如果已提交的任务数大于当前线程数，线程不够用了，返回false去创建新线程
      if (parent.getPoolSize()<parent.getMaximumPoolSize()) 
          return false;
          
      //默认情况下总是把任务添加到任务队列
      return super.offer(o);
  }
  
}
```



### 问题：

Spring如何修改内嵌Tomcat的Executor线程配置？

答：可以的，参考：org.springframework.boot.autoconfigure.web.ServerProperties

```java
@ConfigurationProperties(prefix = "server", ignoreUnknownFields = true)
public class ServerProperties {

    // 可支持优雅关闭 - Shutdown.GRACEFUL
    private Shutdown shutdown = Shutdown.IMMEDIATE;

   	private final Tomcat tomcat = new Tomcat();
    
    // ...
    
    /**
	 * Tomcat properties.
	 */
	public static class Tomcat {
    
		/**
		 * Thread related configuration.
		 */
		private final Threads threads = new Threads();
        
        /**
		 * Maximum number of connections that the server accepts and processes at any
		 * given time. Once the limit has been reached, the operating system may still
		 * accept connections based on the "acceptCount" property.
		 */
		private int maxConnections = 8192;

		/**
		 * Maximum queue length for incoming connection requests when all possible request
		 * processing threads are in use.
		 */
		private int acceptCount = 100;

		/**
		 * Maximum number of idle processors that will be retained in the cache and reused
		 * with a subsequent request. When set to -1 the cache will be unlimited with a
		 * theoretical maximum size equal to the maximum number of connections.
		 */
		private int processorCache = 200;
        
        // ...
        
        /**
		 * Tomcat thread properties.
		 */
		public static class Threads {

			/**
			 * Maximum amount of worker threads.
			 */
			private int max = 200;

			/**
			 * Minimum amount of worker threads.
			 */
			private int minSpare = 10;
        }
    }
}
```
